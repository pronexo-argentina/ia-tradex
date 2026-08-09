package com.iatradex.scanner;

import com.iatradex.ml.MlFilterMode;
import com.iatradex.model.AnalysisResult;
import com.iatradex.model.StrategyType;

public record ScannerResult(
        WatchlistItem item,
        AnalysisResult analysis,
        String regime,
        String volatility,
        Double rsi,
        String trend,
        StrategyType strategy,
        String signal,
        int score,
        String scoreExplanation,
        MlFilterMode mlMode,
        String mlDecision,
        Double mlProbabilityPct,
        String finalDecision,
        String mlExplanation,
        String error
) {
    public boolean successful() {
        return error == null || error.isBlank();
    }

    public boolean mlAvailable() {
        return mlDecision != null
                && !mlDecision.isBlank()
                && !"NO DISPONIBLE".equalsIgnoreCase(mlDecision)
                && !"OFF".equalsIgnoreCase(mlDecision);
    }

    public boolean mlConfirmsEntry() {
        return "FAVORABLE".equalsIgnoreCase(mlDecision);
    }
}
