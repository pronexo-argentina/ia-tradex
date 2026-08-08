package com.iatradex.ml;

public record MlFeature(
        String name,
        double weight,
        double importancePct,
        String interpretation
) {}
