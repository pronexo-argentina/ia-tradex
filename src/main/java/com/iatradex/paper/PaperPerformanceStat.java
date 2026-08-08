package com.iatradex.paper;

public record PaperPerformanceStat(
        String group,
        int trades,
        int wins,
        double winRatePct,
        double pnl,
        double avgPnlPct,
        Double profitFactor,
        double maxRealizedDrawdown,
        double bestTrade,
        double worstTrade
) {}
