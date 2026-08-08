package com.iatradex.paper;

public record PaperPerformanceSummary(
        String currency,
        int closedTrades,
        int wins,
        int losses,
        double realizedPnl,
        double unrealizedPnl,
        double equity,
        double returnPct,
        double winRatePct,
        Double profitFactor,
        double maxRealizedDrawdown,
        double bestTrade,
        double worstTrade
) {}
