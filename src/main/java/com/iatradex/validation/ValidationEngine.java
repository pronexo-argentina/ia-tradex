package com.iatradex.validation;

import com.iatradex.analysis.BacktestEngine;
import com.iatradex.analysis.IndicatorEngine;
import com.iatradex.model.AnalysisResult;
import com.iatradex.model.BacktestResult;
import com.iatradex.model.Candle;
import com.iatradex.model.Metrics;
import com.iatradex.model.StrategyType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ValidationEngine {

    private static final double INITIAL_CASH = 10_000.0;
    private static final double FEE = 0.001;
    private static final double SLIPPAGE = 0.0005;

    private final BacktestEngine backtest = new BacktestEngine();
    private final IndicatorEngine indicators = new IndicatorEngine();

    public ValidationReport validate(AnalysisResult analysis) {
        List<Candle> raw = analysis.candles();

        if (raw == null || raw.size() < 80) {
            throw new IllegalArgumentException(
                    "Se necesitan al menos 80 velas para una validación útil. "
                            + "Elegí un período histórico mayor."
            );
        }

        int split = Math.max(
                50,
                Math.min(raw.size() - 25, (int) Math.floor(raw.size() * 0.70))
        );

        List<Candle> inSample = copySlice(raw, 0, split);
        List<Candle> outOfSample = copySlice(raw, split, raw.size());

        List<ValidationRow> rows = new ArrayList<>();
        List<OptimizationResult> optimizations = new ArrayList<>();

        for (StrategyType strategy : StrategyType.values()) {
            BacktestResult train = run(
                    inSample,
                    analysis,
                    strategy,
                    0.01,
                    0.02,
                    0.04
            );

            BacktestResult test = run(
                    outOfSample,
                    analysis,
                    strategy,
                    0.01,
                    0.02,
                    0.04
            );

            WalkForwardSummary wf = walkForward(
                    raw,
                    analysis,
                    strategy
            );

            double robustnessScore = robustnessScore(
                    train.metrics(),
                    test.metrics(),
                    wf
            );

            ValidationClassification classification = classify(
                    train.metrics(),
                    test.metrics(),
                    wf,
                    robustnessScore
            );

            rows.add(new ValidationRow(
                    strategy,
                    train.metrics().returnPct(),
                    test.metrics().returnPct(),
                    test.metrics().buyHoldReturnPct(),
                    wf.averageReturnPct(),
                    wf.positiveFolds(),
                    wf.folds(),
                    robustnessScore,
                    classification,
                    explanation(
                            train.metrics(),
                            test.metrics(),
                            wf,
                            classification
                    )
            ));

            optimizations.add(
                    optimizeControlled(
                            raw,
                            analysis,
                            strategy
                    )
            );
        }

        rows.sort(
                Comparator.comparingDouble(
                        ValidationRow::robustnessScore
                ).reversed()
        );

        optimizations.sort(
                Comparator.comparingDouble(
                        OptimizationResult::validationReturnPct
                ).reversed()
        );

        return new ValidationReport(
                analysis.symbol(),
                analysis.marketType(),
                analysis.timeframe(),
                analysis.period(),
                raw.size(),
                inSample.size(),
                outOfSample.size(),
                List.copyOf(rows),
                List.copyOf(optimizations),
                Instant.now().toString()
        );
    }

    public void exportCsv(
            ValidationReport report,
            Path target
    ) throws IOException {
        StringBuilder out = new StringBuilder();

        out.append(
                "section,strategy,in_sample_pct,oos_pct,buy_hold_oos_pct,"
                        + "walk_forward_avg_pct,positive_folds,total_folds,"
                        + "robustness_score,classification,risk_pct,stop_pct,"
                        + "take_pct,train_opt_pct,oos_opt_pct\n"
        );

        for (ValidationRow row : report.strategies()) {
            out.append("VALIDATION,")
                    .append(csv(row.strategy().displayName())).append(',')
                    .append(row.inSampleReturnPct()).append(',')
                    .append(row.outOfSampleReturnPct()).append(',')
                    .append(row.outOfSampleBuyHoldPct()).append(',')
                    .append(row.walkForwardAvgPct()).append(',')
                    .append(row.positiveWalkForwardFolds()).append(',')
                    .append(row.walkForwardFolds()).append(',')
                    .append(row.robustnessScore()).append(',')
                    .append(row.classification().name())
                    .append(",,,,,\n");
        }

        for (OptimizationResult row : report.optimization()) {
            out.append("OPTIMIZATION,")
                    .append(csv(row.strategy().displayName()))
                    .append(",,,,,,,,")
                    .append(row.classification().name()).append(',')
                    .append(row.riskPct()).append(',')
                    .append(row.stopLossPct()).append(',')
                    .append(row.takeProfitPct()).append(',')
                    .append(row.trainingReturnPct()).append(',')
                    .append(row.validationReturnPct())
                    .append('\n');
        }

        Files.writeString(
                target,
                out.toString(),
                StandardCharsets.UTF_8
        );
    }

    private String csv(String value) {
        return "\"" + (
                value == null
                        ? ""
                        : value.replace("\"", "\"\"")
        ) + "\"";
    }

    private WalkForwardSummary walkForward(
            List<Candle> raw,
            AnalysisResult analysis,
            StrategyType strategy
    ) {
        int n = raw.size();
        int minimumTrain = Math.max(45, (int) Math.floor(n * 0.45));
        int validationSize = Math.max(15, (n - minimumTrain) / 3);

        List<Double> validationReturns = new ArrayList<>();

        int trainEnd = minimumTrain;

        for (int fold = 0; fold < 3; fold++) {
            int validationStart = trainEnd;
            int validationEnd = fold == 2
                    ? n
                    : Math.min(n, validationStart + validationSize);

            if (validationEnd - validationStart < 12) {
                break;
            }

            List<Candle> train = copySlice(raw, 0, trainEnd);
            List<Candle> validation = copySlice(
                    raw,
                    validationStart,
                    validationEnd
            );

            // En cada fold los parámetros se seleccionan exclusivamente
            // usando el tramo de entrenamiento y luego se congelan para
            // evaluar el bloque siguiente.
            Candidate selected = bestCandidate(
                    train,
                    analysis,
                    strategy
            );

            BacktestResult validationResult = run(
                    validation,
                    analysis,
                    strategy,
                    selected.risk(),
                    selected.stop(),
                    selected.take()
            );

            validationReturns.add(
                    validationResult.metrics().returnPct()
            );

            trainEnd = validationEnd;

            if (trainEnd >= n) {
                break;
            }
        }

        int positive = (int) validationReturns.stream()
                .filter(value -> value > 0.0)
                .count();

        double average = validationReturns.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double worst = validationReturns.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

        return new WalkForwardSummary(
                validationReturns.size(),
                positive,
                average,
                worst
        );
    }

    private OptimizationResult optimizeControlled(
            List<Candle> raw,
            AnalysisResult analysis,
            StrategyType strategy
    ) {
        int split = Math.max(
                50,
                Math.min(raw.size() - 25, (int) Math.floor(raw.size() * 0.70))
        );

        List<Candle> training = copySlice(raw, 0, split);
        List<Candle> validation = copySlice(raw, split, raw.size());

        Candidate best = bestCandidate(
                training,
                analysis,
                strategy
        );

        BacktestResult validationResult = run(
                validation,
                analysis,
                strategy,
                best.risk(),
                best.stop(),
                best.take()
        );

        Metrics vm = validationResult.metrics();

        ValidationClassification classification;

        if (best.metrics().returnPct() > 0.0
                && vm.returnPct() <= 0.0) {
            classification = ValidationClassification.SOBREAJUSTADA;
        } else if (vm.returnPct() > 0.0
                && vm.trades() >= 2
                && Math.abs(vm.maxDrawdownPct()) <= 25.0) {
            classification = ValidationClassification.ROBUSTA;
        } else {
            classification = ValidationClassification.DUDOSA;
        }

        return new OptimizationResult(
                strategy,
                best.risk() * 100.0,
                best.stop() * 100.0,
                best.take() * 100.0,
                best.metrics().returnPct(),
                best.metrics().maxDrawdownPct(),
                best.metrics().trades(),
                vm.returnPct(),
                vm.maxDrawdownPct(),
                vm.trades(),
                vm.buyHoldReturnPct(),
                classification
        );
    }

    private Candidate bestCandidate(
            List<Candle> training,
            AnalysisResult analysis,
            StrategyType strategy
    ) {
        double[] risks = {0.005, 0.010, 0.015};
        double[] stops = {0.015, 0.020, 0.025, 0.030};
        double[] takes = {0.030, 0.040, 0.050, 0.060};

        Candidate best = null;

        for (double risk : risks) {
            for (double stop : stops) {
                for (double take : takes) {
                    if (take <= stop) {
                        continue;
                    }

                    BacktestResult result = run(
                            training,
                            analysis,
                            strategy,
                            risk,
                            stop,
                            take
                    );

                    Metrics m = result.metrics();

                    // Penaliza drawdown y muestras con pocas operaciones.
                    double tradePenalty = m.trades() < 3
                            ? (3 - m.trades()) * 5.0
                            : 0.0;

                    double score = m.returnPct()
                            - Math.abs(m.maxDrawdownPct()) * 0.60
                            - tradePenalty;

                    Candidate candidate = new Candidate(
                            risk,
                            stop,
                            take,
                            score,
                            m
                    );

                    if (best == null
                            || candidate.score() > best.score()) {
                        best = candidate;
                    }
                }
            }
        }

        if (best == null) {
            throw new IllegalStateException(
                    "No se pudo generar una configuración de optimización."
            );
        }

        return best;
    }

    private BacktestResult run(
            List<Candle> candles,
            AnalysisResult analysis,
            StrategyType strategy,
            double risk,
            double stop,
            double take
    ) {
        List<Candle> isolated = copySlice(
                candles,
                0,
                candles.size()
        );

        indicators.apply(isolated);

        return backtest.run(
                isolated,
                analysis.marketType(),
                analysis.timeframe(),
                strategy,
                INITIAL_CASH,
                FEE,
                SLIPPAGE,
                risk,
                stop,
                take
        );
    }

    private List<Candle> copySlice(
            List<Candle> source,
            int from,
            int to
    ) {
        List<Candle> copy = new ArrayList<>();

        for (int i = from; i < to; i++) {
            Candle c = source.get(i);
            copy.add(new Candle(
                    c.timestamp(),
                    c.open(),
                    c.high(),
                    c.low(),
                    c.close(),
                    c.volume()
            ));
        }

        return copy;
    }

    private double robustnessScore(
            Metrics train,
            Metrics test,
            WalkForwardSummary wf
    ) {
        double score = 50.0;

        if (test.returnPct() > 0.0) {
            score += 15.0;
        } else {
            score -= 15.0;
        }

        if (test.returnPct() > test.buyHoldReturnPct()) {
            score += 10.0;
        }

        if (wf.folds() > 0) {
            score += 20.0
                    * wf.positiveFolds()
                    / wf.folds();
        }

        if (wf.averageReturnPct() > 0.0) {
            score += 10.0;
        } else {
            score -= 10.0;
        }

        if (train.returnPct() > 0.0
                && test.returnPct() < train.returnPct() * -0.25) {
            score -= 15.0;
        }

        if (Math.abs(test.maxDrawdownPct()) > 25.0) {
            score -= 10.0;
        }

        return Math.max(0.0, Math.min(100.0, score));
    }

    private ValidationClassification classify(
            Metrics train,
            Metrics test,
            WalkForwardSummary wf,
            double score
    ) {
        if (train.returnPct() > 0.0
                && test.returnPct() <= 0.0
                && wf.averageReturnPct() <= 0.0) {
            return ValidationClassification.SOBREAJUSTADA;
        }

        if (score >= 70.0
                && test.returnPct() > 0.0
                && wf.positiveFolds() >= Math.max(1, wf.folds() - 1)) {
            return ValidationClassification.ROBUSTA;
        }

        return ValidationClassification.DUDOSA;
    }

    private String explanation(
            Metrics train,
            Metrics test,
            WalkForwardSummary wf,
            ValidationClassification classification
    ) {
        return switch (classification) {
            case ROBUSTA ->
                    "Resultado OOS positivo y comportamiento consistente "
                            + "en la mayoría de los tramos Walk-Forward.";
            case SOBREAJUSTADA ->
                    "El rendimiento del tramo de entrenamiento no se sostuvo "
                            + "fuera de muestra.";
            case DUDOSA ->
                    "La evidencia es mixta; necesita más datos, períodos "
                            + "o activos antes de considerarla estable.";
        };
    }

    private record WalkForwardSummary(
            int folds,
            int positiveFolds,
            double averageReturnPct,
            double worstReturnPct
    ) {}

    private record Candidate(
            double risk,
            double stop,
            double take,
            double score,
            Metrics metrics
    ) {}
}
