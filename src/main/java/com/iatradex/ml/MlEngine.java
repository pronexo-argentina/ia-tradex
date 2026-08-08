package com.iatradex.ml;

import com.iatradex.analysis.IndicatorEngine;
import com.iatradex.model.AnalysisResult;
import com.iatradex.model.Candle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MlEngine {

    private static final String[] FEATURE_NAMES = {
            "Retorno 1 vela",
            "Retorno 5 velas",
            "Retorno 20 velas",
            "Separación EMA 12/26",
            "Distancia precio/EMA26",
            "RSI normalizado",
            "ATR % del precio",
            "Rango de vela %",
            "Volumen relativo 20"
    };

    private static final int HORIZON = 5;
    private static final double LABEL_THRESHOLD = 0.005;

    private final IndicatorEngine indicators =
            new IndicatorEngine();

    public MlReport trainAndEvaluate(
            AnalysisResult analysis
    ) {
        List<Candle> candles = copy(analysis.candles());
        indicators.apply(candles);

        int splitCandle = Math.max(
                55,
                Math.min(
                        candles.size() - 30,
                        (int) Math.floor(candles.size() * 0.70)
                )
        );

        List<Sample> samples = buildSamples(candles);
        List<Sample> train = samples.stream()
                // La etiqueta completa debe quedar dentro de In-Sample.
                .filter(sample ->
                        sample.candleIndex() + HORIZON < splitCandle
                )
                .toList();

        List<Sample> test = samples.stream()
                // El OOS empieza recién en el límite temporal reservado.
                .filter(sample ->
                        sample.candleIndex() >= splitCandle
                )
                .toList();

        if (train.size() < 50 || test.size() < 20) {
            throw new IllegalArgumentException(
                    "El modelo necesita más historial para separar "
                            + "entrenamiento y evaluación OOS sin mezclar futuro. "
                            + "Elegí un período mayor."
            );
        }

        Standardization standardization =
                standardization(train);

        LogisticRegressionModel model = trainModel(
                train,
                standardization
        );

        Evaluation evaluation =
                evaluate(model, test);

        double positiveRate = train.stream()
                .mapToInt(Sample::label)
                .average()
                .orElse(0.0);

        double baselineBrier = test.stream()
                .mapToDouble(sample -> {
                    double delta =
                            positiveRate - sample.label();
                    return delta * delta;
                })
                .average()
                .orElse(0.0);

        int currentIndex = candles.size() - 2;

        if (currentIndex < 25) {
            throw new IllegalArgumentException(
                    "No hay suficientes velas para construir "
                            + "las variables actuales."
            );
        }

        double[] currentFeatures =
                features(candles, currentIndex);

        double probability =
                model.probability(currentFeatures);

        String decision;

        if (probability >= 0.62
                && evaluation.brierScore() < baselineBrier) {
            decision = "FAVORABLE";
        } else if (probability <= 0.45) {
            decision = "NO OPERAR";
        } else {
            decision = "OBSERVAR";
        }

        List<MlFeature> importance =
                featureImportance(model.weights());

        String explanation =
                explanation(
                        probability,
                        evaluation,
                        baselineBrier,
                        decision
                );

        return new MlReport(
                analysis.symbol(),
                analysis.timeframe(),
                analysis.period(),
                samples.size(),
                train.size(),
                test.size(),
                HORIZON,
                LABEL_THRESHOLD * 100.0,
                probability * 100.0,
                decision,
                evaluation.accuracy() * 100.0,
                evaluation.precision() * 100.0,
                evaluation.recall() * 100.0,
                evaluation.balancedAccuracy() * 100.0,
                evaluation.brierScore(),
                baselineBrier,
                positiveRate * 100.0,
                importance,
                explanation,
                Instant.now().toString()
        );
    }

    public void exportCsv(
            MlReport report,
            Path target
    ) throws IOException {
        StringBuilder out = new StringBuilder();

        out.append("section,key,value,weight,importance_pct,interpretation\n");

        appendMetric(out, "symbol", report.symbol());
        appendMetric(out, "timeframe", report.timeframe());
        appendMetric(out, "period", report.period());
        appendMetric(out, "samples", report.samples());
        appendMetric(out, "training_samples", report.trainingSamples());
        appendMetric(out, "test_samples", report.testSamples());
        appendMetric(out, "horizon_bars", report.horizonBars());
        appendMetric(out, "positive_label_threshold_pct", report.positiveLabelThresholdPct());
        appendMetric(out, "current_probability_pct", report.currentProbabilityPct());
        appendMetric(out, "decision", report.decision());
        appendMetric(out, "accuracy_pct", report.accuracyPct());
        appendMetric(out, "precision_pct", report.precisionPct());
        appendMetric(out, "recall_pct", report.recallPct());
        appendMetric(out, "balanced_accuracy_pct", report.balancedAccuracyPct());
        appendMetric(out, "brier_score", report.brierScore());
        appendMetric(out, "baseline_brier_score", report.baselineBrierScore());
        appendMetric(out, "positive_rate_pct", report.positiveRatePct());

        for (MlFeature feature : report.features()) {
            out.append("FEATURE,")
                    .append(csv(feature.name())).append(',')
                    .append(',')
                    .append(feature.weight()).append(',')
                    .append(feature.importancePct()).append(',')
                    .append(csv(feature.interpretation()))
                    .append('\n');
        }

        Files.writeString(
                target,
                out.toString(),
                StandardCharsets.UTF_8
        );
    }

    private void appendMetric(
            StringBuilder out,
            String key,
            Object value
    ) {
        out.append("METRIC,")
                .append(csv(key)).append(',')
                .append(csv(String.valueOf(value)))
                .append(",,,\n");
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private List<Sample> buildSamples(
            List<Candle> candles
    ) {
        List<Sample> samples = new ArrayList<>();

        for (int i = 25; i < candles.size() - HORIZON; i++) {
            double[] x = features(candles, i);

            boolean finite = true;

            for (double value : x) {
                if (!Double.isFinite(value)) {
                    finite = false;
                    break;
                }
            }

            if (!finite) {
                continue;
            }

            double currentClose = candles.get(i).close();
            double futureClose =
                    candles.get(i + HORIZON).close();

            if (currentClose <= 0.0 || futureClose <= 0.0) {
                continue;
            }

            double forwardReturn =
                    futureClose / currentClose - 1.0;

            int label = forwardReturn > LABEL_THRESHOLD
                    ? 1
                    : 0;

            samples.add(new Sample(i, x, label));
        }

        return samples;
    }

    private double[] features(
            List<Candle> candles,
            int i
    ) {
        Candle row = candles.get(i);

        double ret1 = returnBetween(
                candles.get(i - 1).close(),
                row.close()
        );

        double ret5 = returnBetween(
                candles.get(i - 5).close(),
                row.close()
        );

        double ret20 = returnBetween(
                candles.get(i - 20).close(),
                row.close()
        );

        double emaSpread =
                safeRatio(row.emaFast(), row.emaSlow()) - 1.0;

        double distanceSlow =
                safeRatio(row.close(), row.emaSlow()) - 1.0;

        double rsi =
                Double.isFinite(row.rsi14())
                        ? (row.rsi14() - 50.0) / 50.0
                        : 0.0;

        double atrPct =
                row.close() <= 0.0
                        || !Double.isFinite(row.atr14())
                        ? 0.0
                        : row.atr14() / row.close();

        double rangePct =
                row.close() <= 0.0
                        ? 0.0
                        : (row.high() - row.low()) / row.close();

        double volumeAvg = 0.0;
        int volumeCount = 0;

        for (int j = i - 20; j < i; j++) {
            double volume = candles.get(j).volume();

            if (Double.isFinite(volume) && volume > 0.0) {
                volumeAvg += volume;
                volumeCount++;
            }
        }

        volumeAvg = volumeCount == 0
                ? 0.0
                : volumeAvg / volumeCount;

        double volumeRelative =
                volumeAvg <= 0.0
                        ? 0.0
                        : row.volume() / volumeAvg - 1.0;

        return new double[]{
                ret1,
                ret5,
                ret20,
                emaSpread,
                distanceSlow,
                rsi,
                atrPct,
                rangePct,
                volumeRelative
        };
    }

    private LogisticRegressionModel trainModel(
            List<Sample> train,
            Standardization standardization
    ) {
        int dimensions = FEATURE_NAMES.length;
        double[] weights = new double[dimensions];
        double bias = 0.0;

        double learningRate = 0.035;
        double l2 = 0.002;
        int epochs = 1400;

        double positive = train.stream()
                .mapToInt(Sample::label)
                .average()
                .orElse(0.5);

        double positiveWeight = positive <= 0.0
                ? 1.0
                : Math.min(5.0, 0.5 / positive);

        double negativeWeight = positive >= 1.0
                ? 1.0
                : Math.min(5.0, 0.5 / (1.0 - positive));

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] grad = new double[dimensions];
            double gradBias = 0.0;

            for (Sample sample : train) {
                double[] x = normalize(
                        sample.features(),
                        standardization
                );

                double z = bias;

                for (int j = 0; j < dimensions; j++) {
                    z += weights[j] * x[j];
                }

                double p = sigmoid(z);
                double sampleWeight = sample.label() == 1
                        ? positiveWeight
                        : negativeWeight;

                double error =
                        (p - sample.label()) * sampleWeight;

                gradBias += error;

                for (int j = 0; j < dimensions; j++) {
                    grad[j] += error * x[j];
                }
            }

            double n = train.size();

            for (int j = 0; j < dimensions; j++) {
                double gradient =
                        grad[j] / n + l2 * weights[j];
                weights[j] -= learningRate * gradient;
            }

            bias -= learningRate * gradBias / n;
        }

        return new LogisticRegressionModel(
                weights,
                bias,
                standardization.means(),
                standardization.stds()
        );
    }

    private Evaluation evaluate(
            LogisticRegressionModel model,
            List<Sample> test
    ) {
        int tp = 0;
        int tn = 0;
        int fp = 0;
        int fn = 0;
        double brier = 0.0;

        for (Sample sample : test) {
            double probability =
                    model.probability(sample.features());

            int predicted = probability >= 0.5
                    ? 1
                    : 0;

            if (predicted == 1 && sample.label() == 1) tp++;
            if (predicted == 0 && sample.label() == 0) tn++;
            if (predicted == 1 && sample.label() == 0) fp++;
            if (predicted == 0 && sample.label() == 1) fn++;

            double delta =
                    probability - sample.label();
            brier += delta * delta;
        }

        int total = test.size();

        double accuracy = total == 0
                ? 0.0
                : (double) (tp + tn) / total;

        double precision = tp + fp == 0
                ? 0.0
                : (double) tp / (tp + fp);

        double recall = tp + fn == 0
                ? 0.0
                : (double) tp / (tp + fn);

        double specificity = tn + fp == 0
                ? 0.0
                : (double) tn / (tn + fp);

        double balancedAccuracy =
                (recall + specificity) / 2.0;

        return new Evaluation(
                accuracy,
                precision,
                recall,
                balancedAccuracy,
                total == 0 ? 0.0 : brier / total
        );
    }

    private Standardization standardization(
            List<Sample> train
    ) {
        int dimensions = FEATURE_NAMES.length;
        double[] means = new double[dimensions];
        double[] stds = new double[dimensions];

        for (Sample sample : train) {
            for (int j = 0; j < dimensions; j++) {
                means[j] += sample.features()[j];
            }
        }

        for (int j = 0; j < dimensions; j++) {
            means[j] /= train.size();
        }

        for (Sample sample : train) {
            for (int j = 0; j < dimensions; j++) {
                double delta =
                        sample.features()[j] - means[j];
                stds[j] += delta * delta;
            }
        }

        for (int j = 0; j < dimensions; j++) {
            stds[j] = Math.sqrt(
                    stds[j] / Math.max(1, train.size() - 1)
            );

            if (!Double.isFinite(stds[j])
                    || stds[j] < 1e-12) {
                stds[j] = 1.0;
            }
        }

        return new Standardization(means, stds);
    }

    private double[] normalize(
            double[] values,
            Standardization standardization
    ) {
        double[] normalized =
                new double[values.length];

        for (int i = 0; i < values.length; i++) {
            normalized[i] =
                    (values[i] - standardization.means()[i])
                            / standardization.stds()[i];
        }

        return normalized;
    }

    private List<MlFeature> featureImportance(
            double[] weights
    ) {
        double total = 0.0;

        for (double weight : weights) {
            total += Math.abs(weight);
        }

        final double denominator =
                total <= 0.0 ? 1.0 : total;

        List<MlFeature> features =
                new ArrayList<>();

        for (int i = 0; i < weights.length; i++) {
            double weight = weights[i];

            features.add(new MlFeature(
                    FEATURE_NAMES[i],
                    weight,
                    Math.abs(weight) / denominator * 100.0,
                    weight >= 0.0
                            ? "Aporta hacia condición favorable"
                            : "Aporta hacia condición desfavorable"
            ));
        }

        features.sort(
                Comparator.comparingDouble(
                        MlFeature::importancePct
                ).reversed()
        );

        return List.copyOf(features);
    }

    private String explanation(
            double probability,
            Evaluation evaluation,
            double baselineBrier,
            String decision
    ) {
        String quality;

        if (evaluation.brierScore() < baselineBrier * 0.90
                && evaluation.balancedAccuracy() >= 0.55) {
            quality = "El modelo mejora claramente al baseline OOS.";
        } else if (evaluation.brierScore() < baselineBrier) {
            quality = "El modelo mejora ligeramente al baseline OOS.";
        } else {
            quality = "El modelo no supera al baseline OOS; la señal debe considerarse débil.";
        }

        return "Decisión "
                + decision
                + " con probabilidad estimada "
                + String.format("%.1f%%", probability * 100.0)
                + ". "
                + quality
                + " La probabilidad describe la etiqueta aprendida, no la probabilidad real de ganar una operación.";
    }

    private double returnBetween(
            double previous,
            double current
    ) {
        if (previous <= 0.0 || current <= 0.0) {
            return 0.0;
        }

        return current / previous - 1.0;
    }

    private double safeRatio(
            double numerator,
            double denominator
    ) {
        if (!Double.isFinite(numerator)
                || !Double.isFinite(denominator)
                || denominator == 0.0) {
            return 1.0;
        }

        return numerator / denominator;
    }

    private List<Candle> copy(
            List<Candle> source
    ) {
        List<Candle> copy = new ArrayList<>();

        for (Candle candle : source) {
            copy.add(new Candle(
                    candle.timestamp(),
                    candle.open(),
                    candle.high(),
                    candle.low(),
                    candle.close(),
                    candle.volume()
            ));
        }

        return copy;
    }

    private double sigmoid(double value) {
        if (value >= 0.0) {
            double exp = Math.exp(-value);
            return 1.0 / (1.0 + exp);
        }

        double exp = Math.exp(value);
        return exp / (1.0 + exp);
    }

    private record Sample(
            int candleIndex,
            double[] features,
            int label
    ) {}

    private record Standardization(
            double[] means,
            double[] stds
    ) {}

    private record Evaluation(
            double accuracy,
            double precision,
            double recall,
            double balancedAccuracy,
            double brierScore
    ) {}
}
