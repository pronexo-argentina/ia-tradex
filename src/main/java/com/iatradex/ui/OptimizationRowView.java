package com.iatradex.ui;

import com.iatradex.validation.OptimizationResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class OptimizationRowView {

    private final StringProperty strategy;
    private final StringProperty risk;
    private final StringProperty stop;
    private final StringProperty take;
    private final StringProperty train;
    private final StringProperty validation;
    private final StringProperty buyHold;
    private final StringProperty trades;
    private final StringProperty classification;

    public OptimizationRowView(OptimizationResult result) {
        strategy = new SimpleStringProperty(
                result.strategy().displayName()
        );
        risk = new SimpleStringProperty(
                String.format("%.2f%%", result.riskPct())
        );
        stop = new SimpleStringProperty(
                String.format("%.2f%%", result.stopLossPct())
        );
        take = new SimpleStringProperty(
                String.format("%.2f%%", result.takeProfitPct())
        );
        train = new SimpleStringProperty(
                String.format("%+.2f%%", result.trainingReturnPct())
        );
        validation = new SimpleStringProperty(
                String.format("%+.2f%%", result.validationReturnPct())
        );
        buyHold = new SimpleStringProperty(
                String.format("%+.2f%%", result.buyHoldValidationPct())
        );
        trades = new SimpleStringProperty(
                result.trainingTrades()
                        + " / "
                        + result.validationTrades()
        );
        classification = new SimpleStringProperty(
                result.classification().name()
        );
    }

    public StringProperty strategyProperty() { return strategy; }
    public StringProperty riskProperty() { return risk; }
    public StringProperty stopProperty() { return stop; }
    public StringProperty takeProperty() { return take; }
    public StringProperty trainProperty() { return train; }
    public StringProperty validationProperty() { return validation; }
    public StringProperty buyHoldProperty() { return buyHold; }
    public StringProperty tradesProperty() { return trades; }
    public StringProperty classificationProperty() { return classification; }
}
