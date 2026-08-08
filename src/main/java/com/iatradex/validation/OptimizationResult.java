package com.iatradex.validation;

import com.iatradex.model.StrategyType;

public record OptimizationResult(
        StrategyType strategy,
        double riskPct,
        double stopLossPct,
        double takeProfitPct,
        double trainingReturnPct,
        double trainingDrawdownPct,
        int trainingTrades,
        double validationReturnPct,
        double validationDrawdownPct,
        int validationTrades,
        double buyHoldValidationPct,
        ValidationClassification classification
) {}
