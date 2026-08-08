package com.iatradex.ml;

import java.util.Arrays;

public final class LogisticRegressionModel {

    private final double[] weights;
    private final double bias;
    private final double[] means;
    private final double[] stds;

    public LogisticRegressionModel(
            double[] weights,
            double bias,
            double[] means,
            double[] stds
    ) {
        this.weights = Arrays.copyOf(weights, weights.length);
        this.bias = bias;
        this.means = Arrays.copyOf(means, means.length);
        this.stds = Arrays.copyOf(stds, stds.length);
    }

    public double probability(double[] rawFeatures) {
        if (rawFeatures.length != weights.length) {
            throw new IllegalArgumentException(
                    "Cantidad de variables incompatible con el modelo."
            );
        }

        double z = bias;

        for (int i = 0; i < weights.length; i++) {
            double std = stds[i] == 0.0 ? 1.0 : stds[i];
            double normalized =
                    (rawFeatures[i] - means[i]) / std;
            z += weights[i] * normalized;
        }

        return sigmoid(z);
    }

    public double[] weights() {
        return Arrays.copyOf(weights, weights.length);
    }

    private double sigmoid(double value) {
        if (value >= 0.0) {
            double exp = Math.exp(-value);
            return 1.0 / (1.0 + exp);
        }

        double exp = Math.exp(value);
        return exp / (1.0 + exp);
    }
}
