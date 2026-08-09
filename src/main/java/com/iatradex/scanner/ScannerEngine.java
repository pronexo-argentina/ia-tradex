package com.iatradex.scanner;

import com.iatradex.analysis.AnalysisService;
import com.iatradex.analysis.StrategySignalEngine;
import com.iatradex.ml.MlEngine;
import com.iatradex.ml.MlFilterMode;
import com.iatradex.ml.MlReport;
import com.iatradex.model.AnalysisResult;
import com.iatradex.model.Candle;
import com.iatradex.model.MarketRegime;
import com.iatradex.model.StrategyPerformance;
import com.iatradex.model.StrategyType;
import com.iatradex.model.TechnicalSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ScannerEngine {

    private final AnalysisService analysisService;
    private final StrategySignalEngine signals =
            new StrategySignalEngine();
    private final MlEngine mlEngine = new MlEngine();

    public ScannerEngine(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    public ScannerResult scan(WatchlistItem item) {
        return scan(item, MlFilterMode.DISABLED);
    }

    public ScannerResult scan(
            WatchlistItem item,
            MlFilterMode mlMode
    ) {
        MlFilterMode effectiveMode = mlMode == null
                ? MlFilterMode.DISABLED
                : mlMode;

        try {
            AnalysisResult analysis = analysisService.analyze(
                    item.marketType(),
                    item.source(),
                    item.symbol(),
                    item.timeframe(),
                    item.period(),
                    item.currency()
            );

            ScannerResult technical =
                    score(item, analysis, effectiveMode);

            if (effectiveMode == MlFilterMode.DISABLED) {
                return technical;
            }

            return applyMl(
                    technical,
                    effectiveMode
            );
        } catch (Exception ex) {
            return errorResult(
                    item,
                    effectiveMode,
                    ex
            );
        }
    }

    private ScannerResult applyMl(
            ScannerResult technical,
            MlFilterMode mode
    ) {
        try {
            MlReport report =
                    mlEngine.trainAndEvaluate(
                            technical.analysis()
                    );

            String finalDecision =
                    technical.signal();

            if (mode == MlFilterMode.CONFIRMATION
                    && "ENTRADA".equals(technical.signal())
                    && !"FAVORABLE".equals(report.decision())) {
                finalDecision = "BLOQUEADA ML";
            }

            String explanation =
                    "ML "
                            + report.decision()
                            + " · "
                            + String.format(
                                    "%.1f%%",
                                    report.currentProbabilityPct()
                            )
                            + " · Balanced Acc. "
                            + String.format(
                                    "%.1f%%",
                                    report.balancedAccuracyPct()
                            )
                            + " · Brier "
                            + String.format(
                                    "%.4f",
                                    report.brierScore()
                            )
                            + " vs baseline "
                            + String.format(
                                    "%.4f",
                                    report.baselineBrierScore()
                            );

            return new ScannerResult(
                    technical.item(),
                    technical.analysis(),
                    technical.regime(),
                    technical.volatility(),
                    technical.rsi(),
                    technical.trend(),
                    technical.strategy(),
                    technical.signal(),
                    technical.score(),
                    technical.scoreExplanation(),
                    mode,
                    report.decision(),
                    report.currentProbabilityPct(),
                    finalDecision,
                    explanation,
                    null
            );
        } catch (Exception ex) {
            String mlError = ex.getMessage() == null
                    ? ex.getClass().getSimpleName()
                    : ex.getMessage();

            String finalDecision =
                    technical.signal();

            if (mode == MlFilterMode.CONFIRMATION
                    && "ENTRADA".equals(technical.signal())) {
                finalDecision = "BLOQUEADA ML";
            }

            return new ScannerResult(
                    technical.item(),
                    technical.analysis(),
                    technical.regime(),
                    technical.volatility(),
                    technical.rsi(),
                    technical.trend(),
                    technical.strategy(),
                    technical.signal(),
                    technical.score(),
                    technical.scoreExplanation(),
                    mode,
                    "NO DISPONIBLE",
                    null,
                    finalDecision,
                    "ML no disponible: " + mlError,
                    null
            );
        }
    }

    private ScannerResult score(
            WatchlistItem item,
            AnalysisResult analysis,
            MlFilterMode mode
    ) {
        TechnicalSnapshot technical = analysis.technical();
        MarketRegime regime = analysis.regime();
        List<Candle> candles = analysis.candles();
        int signalIndex = Math.max(1, candles.size() - 2);

        List<StrategyType> entryStrategies = new ArrayList<>();

        for (StrategyType type : StrategyType.values()) {
            if (signals.entrySignal(candles, signalIndex, type)) {
                entryStrategies.add(type);
            }
        }

        StrategyType selectedStrategy;

        if (!entryStrategies.isEmpty()) {
            selectedStrategy = entryStrategies.stream()
                    .max(Comparator.comparingDouble(
                            type -> historicalReturn(analysis, type)
                    ))
                    .orElse(entryStrategies.get(0));
        } else {
            selectedStrategy = regime.compatibleStrategies().stream()
                    .max(Comparator.comparingDouble(
                            type -> historicalReturn(analysis, type)
                    ))
                    .orElse(analysis.bestHistoricalStrategy());
        }

        int score = 0;
        List<String> why = new ArrayList<>();

        if ("ALCISTA".equalsIgnoreCase(regime.trend())) {
            score += 24;
            why.add("+24 tendencia alcista");
        } else if ("LATERAL".equalsIgnoreCase(regime.trend())) {
            score += 10;
            why.add("+10 mercado lateral");
        } else {
            why.add("+0 tendencia bajista");
        }

        if ("ALTA".equalsIgnoreCase(regime.strength())) {
            score += 14;
            why.add("+14 fuerza alta");
        } else if ("MEDIA".equalsIgnoreCase(regime.strength())) {
            score += 9;
            why.add("+9 fuerza media");
        } else {
            score += 3;
            why.add("+3 fuerza baja");
        }

        Double rsi = technical.rsi14();

        if (rsi != null && rsi >= 50.0 && rsi < 70.0) {
            score += 16;
            why.add("+16 RSI favorable");
        } else if (rsi != null && rsi >= 35.0 && rsi < 50.0) {
            score += 8;
            why.add("+8 RSI neutral");
        } else if (rsi != null && rsi <= 35.0
                && selectedStrategy == StrategyType.MEAN_REVERSION) {
            score += 15;
            why.add("+15 RSI de reversión");
        } else {
            why.add("+0 RSI sin ventaja clara");
        }

        if (regime.compatibleStrategies().contains(selectedStrategy)) {
            score += 14;
            why.add("+14 estrategia compatible");
        }

        boolean entry = entryStrategies.contains(selectedStrategy);

        if (entry) {
            score += 22;
            why.add("+22 señal de entrada");
        }

        double histReturn = historicalReturn(
                analysis,
                selectedStrategy
        );

        if (histReturn > 0.0) {
            int historicalPoints = (int) Math.min(
                    10,
                    Math.round(histReturn)
            );
            score += historicalPoints;
            why.add(
                    "+" + historicalPoints
                            + " retorno histórico positivo"
            );
        }

        if ("ALTA".equalsIgnoreCase(regime.volatility())) {
            score -= 5;
            why.add("-5 volatilidad alta");
        } else if ("BAJA".equalsIgnoreCase(regime.volatility())) {
            score += 4;
            why.add("+4 volatilidad baja");
        }

        score = Math.max(0, Math.min(100, score));

        String signal = entry
                ? "ENTRADA"
                : technical.signal().contains("COMPRA")
                ? "OBSERVAR"
                : "ESPERAR";

        return new ScannerResult(
                item,
                analysis,
                regime.trend(),
                regime.volatility(),
                rsi,
                technical.trend(),
                selectedStrategy,
                signal,
                score,
                String.join(" · ", why),
                mode,
                mode == MlFilterMode.DISABLED
                        ? "OFF"
                        : "PENDIENTE",
                null,
                signal,
                mode == MlFilterMode.DISABLED
                        ? "ML desactivado."
                        : "ML pendiente.",
                null
        );
    }

    private ScannerResult errorResult(
            WatchlistItem item,
            MlFilterMode mode,
            Exception ex
    ) {
        return new ScannerResult(
                item,
                null,
                "—",
                "—",
                null,
                "—",
                StrategyType.EMA_CROSS,
                "ERROR",
                0,
                "No se pudo analizar el activo.",
                mode,
                mode == MlFilterMode.DISABLED
                        ? "OFF"
                        : "NO DISPONIBLE",
                null,
                "ERROR",
                "ML no evaluado.",
                ex.getMessage() == null
                        ? ex.getClass().getSimpleName()
                        : ex.getMessage()
        );
    }

    private double historicalReturn(
            AnalysisResult analysis,
            StrategyType type
    ) {
        return analysis.strategies().stream()
                .filter(s -> s.strategy() == type)
                .map(StrategyPerformance::metrics)
                .mapToDouble(m -> m.returnPct())
                .findFirst()
                .orElse(0.0);
    }
}
