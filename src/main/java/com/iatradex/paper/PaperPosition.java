package com.iatradex.paper;

public record PaperPosition(
        String id,
        String symbol,
        String market,
        String currency,
        double quantity,
        double entryPrice,
        Double lastPrice,
        Double stopLoss,
        Double takeProfit,
        String strategyContext,
        String regimeContext,
        String openedAt
) {
    public PaperPosition withLastPrice(Double price) {
        return new PaperPosition(
                id, symbol, market, currency, quantity, entryPrice,
                price, stopLoss, takeProfit, strategyContext,
                regimeContext, openedAt
        );
    }

    public double marketValue() {
        double px = lastPrice == null ? entryPrice : lastPrice;
        return quantity * px;
    }

    public double unrealizedPnl() {
        double px = lastPrice == null ? entryPrice : lastPrice;
        return quantity * (px - entryPrice);
    }

    public double unrealizedPnlPct() {
        if (entryPrice == 0.0) return 0.0;
        double px = lastPrice == null ? entryPrice : lastPrice;
        return (px / entryPrice - 1.0) * 100.0;
    }
}
