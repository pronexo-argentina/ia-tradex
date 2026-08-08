package com.iatradex.validation;

import java.util.List;

public record ValidationReport(
        String symbol,
        String marketType,
        String timeframe,
        String period,
        int candles,
        int inSampleCandles,
        int outOfSampleCandles,
        List<ValidationRow> strategies,
        List<OptimizationResult> optimization,
        String generatedAt
) {}
