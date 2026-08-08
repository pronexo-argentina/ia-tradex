package com.iatradex.validation;

import com.iatradex.model.StrategyType;

public record ValidationRow(
        StrategyType strategy,
        double inSampleReturnPct,
        double outOfSampleReturnPct,
        double outOfSampleBuyHoldPct,
        double walkForwardAvgPct,
        int positiveWalkForwardFolds,
        int walkForwardFolds,
        double robustnessScore,
        ValidationClassification classification,
        String explanation
) {}
