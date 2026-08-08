package com.iatradex.ui;

import com.iatradex.paper.PaperPerformanceStat;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class PaperPerformanceRow {

    private final StringProperty group;
    private final StringProperty trades;
    private final StringProperty winRate;
    private final StringProperty pnl;
    private final StringProperty avgPnlPct;
    private final StringProperty profitFactor;
    private final StringProperty drawdown;
    private final StringProperty best;
    private final StringProperty worst;

    public PaperPerformanceRow(
            PaperPerformanceStat stat,
            String currency
    ) {
        String prefix = "ARS".equalsIgnoreCase(currency)
                ? "AR$"
                : "US$";

        group = new SimpleStringProperty(stat.group());
        trades = new SimpleStringProperty(
                String.valueOf(stat.trades())
        );
        winRate = new SimpleStringProperty(
                String.format("%.2f%%", stat.winRatePct())
        );
        pnl = new SimpleStringProperty(
                String.format("%s %,.2f", prefix, stat.pnl())
        );
        avgPnlPct = new SimpleStringProperty(
                String.format("%+.2f%%", stat.avgPnlPct())
        );
        profitFactor = new SimpleStringProperty(
                stat.profitFactor() == null
                        ? "∞"
                        : String.format("%.2f", stat.profitFactor())
        );
        drawdown = new SimpleStringProperty(
                String.format(
                        "%s %,.2f",
                        prefix,
                        stat.maxRealizedDrawdown()
                )
        );
        best = new SimpleStringProperty(
                String.format("%s %,.2f", prefix, stat.bestTrade())
        );
        worst = new SimpleStringProperty(
                String.format("%s %,.2f", prefix, stat.worstTrade())
        );
    }

    public StringProperty groupProperty() { return group; }
    public StringProperty tradesProperty() { return trades; }
    public StringProperty winRateProperty() { return winRate; }
    public StringProperty pnlProperty() { return pnl; }
    public StringProperty avgPnlPctProperty() { return avgPnlPct; }
    public StringProperty profitFactorProperty() { return profitFactor; }
    public StringProperty drawdownProperty() { return drawdown; }
    public StringProperty bestProperty() { return best; }
    public StringProperty worstProperty() { return worst; }
}
