package com.iatradex.ml;

public record MlDecisionStats(
        int total,
        int pending,
        int resolved,
        int actionableResolved,
        int correct,
        double accuracyPct,
        int favorableResolved,
        int favorableCorrect,
        double favorableAccuracyPct,
        int noOperateResolved,
        int noOperateCorrect,
        double noOperateAccuracyPct,
        double avgForwardReturnPct
) {}
