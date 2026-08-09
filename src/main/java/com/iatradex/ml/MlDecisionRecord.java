package com.iatradex.ml;

public record MlDecisionRecord(
        String id,
        String key,
        String symbol,
        String marketType,
        String source,
        String currency,
        String timeframe,
        String period,
        long decisionTimestamp,
        double referenceClose,
        int horizonBars,
        double thresholdPct,
        double probabilityPct,
        String decision,
        double balancedAccuracyPct,
        double brierScore,
        double baselineBrierScore,
        String status,
        Long targetTimestamp,
        Double targetClose,
        Double forwardReturnPct,
        Boolean actualPositive,
        Boolean correct,
        String recordedAt,
        String resolvedAt
) {
    public boolean pending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean resolved() {
        return "RESOLVED".equalsIgnoreCase(status);
    }

    public boolean actionable() {
        return "FAVORABLE".equalsIgnoreCase(decision)
                || "NO OPERAR".equalsIgnoreCase(decision);
    }
}
