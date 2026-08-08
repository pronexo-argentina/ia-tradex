package com.iatradex.ui;

import com.iatradex.validation.ValidationRow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ValidationRowView {

    private final StringProperty strategy;
    private final StringProperty inSample;
    private final StringProperty outSample;
    private final StringProperty buyHold;
    private final StringProperty walkForward;
    private final StringProperty folds;
    private final StringProperty score;
    private final StringProperty classification;
    private final StringProperty explanation;

    public ValidationRowView(ValidationRow row) {
        strategy = new SimpleStringProperty(
                row.strategy().displayName()
        );
        inSample = new SimpleStringProperty(
                String.format("%+.2f%%", row.inSampleReturnPct())
        );
        outSample = new SimpleStringProperty(
                String.format("%+.2f%%", row.outOfSampleReturnPct())
        );
        buyHold = new SimpleStringProperty(
                String.format("%+.2f%%", row.outOfSampleBuyHoldPct())
        );
        walkForward = new SimpleStringProperty(
                String.format("%+.2f%%", row.walkForwardAvgPct())
        );
        folds = new SimpleStringProperty(
                row.positiveWalkForwardFolds()
                        + "/"
                        + row.walkForwardFolds()
        );
        score = new SimpleStringProperty(
                String.format("%.0f/100", row.robustnessScore())
        );
        classification = new SimpleStringProperty(
                row.classification().name()
        );
        explanation = new SimpleStringProperty(
                row.explanation()
        );
    }

    public StringProperty strategyProperty() { return strategy; }
    public StringProperty inSampleProperty() { return inSample; }
    public StringProperty outSampleProperty() { return outSample; }
    public StringProperty buyHoldProperty() { return buyHold; }
    public StringProperty walkForwardProperty() { return walkForward; }
    public StringProperty foldsProperty() { return folds; }
    public StringProperty scoreProperty() { return score; }
    public StringProperty classificationProperty() { return classification; }
    public StringProperty explanationProperty() { return explanation; }
}
