package com.iatradex.ml;

import java.util.List;

public record MlReport(
        String symbol,
        String timeframe,
        String period,
        int samples,
        int trainingSamples,
        int testSamples,
        int horizonBars,
        double positiveLabelThresholdPct,
        double currentProbabilityPct,
        String decision,
        double accuracyPct,
        double precisionPct,
        double recallPct,
        double balancedAccuracyPct,
        double brierScore,
        double baselineBrierScore,
        double positiveRatePct,
        List<MlFeature> features,
        String explanation,
        String generatedAt
) {}
