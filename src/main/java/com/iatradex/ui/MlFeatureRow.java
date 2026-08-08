package com.iatradex.ui;

import com.iatradex.ml.MlFeature;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class MlFeatureRow {

    private final StringProperty feature;
    private final StringProperty weight;
    private final StringProperty importance;
    private final StringProperty interpretation;

    public MlFeatureRow(MlFeature feature) {
        this.feature = new SimpleStringProperty(
                feature.name()
        );
        this.weight = new SimpleStringProperty(
                String.format("%+.4f", feature.weight())
        );
        this.importance = new SimpleStringProperty(
                String.format("%.1f%%", feature.importancePct())
        );
        this.interpretation = new SimpleStringProperty(
                feature.interpretation()
        );
    }

    public StringProperty featureProperty() { return feature; }
    public StringProperty weightProperty() { return weight; }
    public StringProperty importanceProperty() { return importance; }
    public StringProperty interpretationProperty() { return interpretation; }
}
