package com.iatradex.ml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iatradex.model.AnalysisResult;
import com.iatradex.model.Candle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class MlDecisionService {

    private static final int MAX_DECISIONS = 2000;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path storage = Path.of(
            System.getProperty("user.home"),
            ".ia-tradex",
            "ml-decisions.json"
    );

    private MlDecisionState state;

    public MlDecisionService() {
        state = load();
    }

    public synchronized void observe(
            AnalysisResult analysis,
            MlReport report
    ) {
        state = load();

        if (analysis == null || report == null) {
            return;
        }

        boolean changed = resolveExisting(
                analysis
        );

        String key = decisionKey(
                analysis,
                report.decisionTimestamp()
        );

        boolean exists = state.decisions.stream()
                .anyMatch(d -> key.equals(d.key()));

        if (!exists) {
            state.decisions.add(
                    new MlDecisionRecord(
                            UUID.randomUUID().toString(),
                            key,
                            analysis.symbol(),
                            analysis.marketType(),
                            analysis.source(),
                            analysis.currency(),
                            analysis.timeframe(),
                            analysis.period(),
                            report.decisionTimestamp(),
                            report.referenceClose(),
                            report.horizonBars(),
                            report.positiveLabelThresholdPct(),
                            report.currentProbabilityPct(),
                            report.decision(),
                            report.balancedAccuracyPct(),
                            report.brierScore(),
                            report.baselineBrierScore(),
                            "PENDING",
                            null,
                            null,
                            null,
                            null,
                            null,
                            Instant.now().toString(),
                            null
                    )
            );
            changed = true;
        }

        trim();

        if (changed) {
            saveUnchecked();
        }
    }

    public synchronized int resolveWithAnalysis(
            AnalysisResult analysis
    ) {
        state = load();
        boolean changed = resolveExisting(analysis);

        if (changed) {
            saveUnchecked();
        }

        return (int) state.decisions.stream()
                .filter(MlDecisionRecord::resolved)
                .count();
    }

    public synchronized List<MlDecisionRecord> decisions() {
        state = load();
        return state.decisions.stream()
                .sorted(
                        Comparator.comparingLong(
                                MlDecisionRecord::decisionTimestamp
                        ).reversed()
                )
                .toList();
    }

    public synchronized MlDecisionStats stats() {
        state = load();
        List<MlDecisionRecord> all =
                List.copyOf(state.decisions);

        int total = all.size();
        int pending = (int) all.stream()
                .filter(MlDecisionRecord::pending)
                .count();
        int resolved = (int) all.stream()
                .filter(MlDecisionRecord::resolved)
                .count();

        List<MlDecisionRecord> actionable = all.stream()
                .filter(MlDecisionRecord::resolved)
                .filter(MlDecisionRecord::actionable)
                .toList();

        int correct = (int) actionable.stream()
                .filter(d -> Boolean.TRUE.equals(d.correct()))
                .count();

        List<MlDecisionRecord> favorable = actionable.stream()
                .filter(d -> "FAVORABLE".equalsIgnoreCase(d.decision()))
                .toList();

        int favorableCorrect = (int) favorable.stream()
                .filter(d -> Boolean.TRUE.equals(d.correct()))
                .count();

        List<MlDecisionRecord> noOperate = actionable.stream()
                .filter(d -> "NO OPERAR".equalsIgnoreCase(d.decision()))
                .toList();

        int noOperateCorrect = (int) noOperate.stream()
                .filter(d -> Boolean.TRUE.equals(d.correct()))
                .count();

        double avgForward = all.stream()
                .filter(MlDecisionRecord::resolved)
                .filter(d -> d.forwardReturnPct() != null)
                .mapToDouble(MlDecisionRecord::forwardReturnPct)
                .average()
                .orElse(0.0);

        return new MlDecisionStats(
                total,
                pending,
                resolved,
                actionable.size(),
                correct,
                pct(correct, actionable.size()),
                favorable.size(),
                favorableCorrect,
                pct(favorableCorrect, favorable.size()),
                noOperate.size(),
                noOperateCorrect,
                pct(noOperateCorrect, noOperate.size()),
                avgForward
        );
    }

    public synchronized void exportCsv(
            Path target
    ) throws IOException {
        state = load();
        StringBuilder out = new StringBuilder();

        out.append(
                "symbol,market,source,currency,timeframe,period,"
                        + "decision_timestamp,reference_close,horizon_bars,"
                        + "threshold_pct,probability_pct,decision,"
                        + "balanced_accuracy_pct,brier_score,baseline_brier_score,"
                        + "status,target_timestamp,target_close,forward_return_pct,"
                        + "actual_positive,correct,recorded_at,resolved_at\n"
        );

        for (MlDecisionRecord d : decisions()) {
            out.append(csv(d.symbol())).append(',')
                    .append(csv(d.marketType())).append(',')
                    .append(csv(d.source())).append(',')
                    .append(csv(d.currency())).append(',')
                    .append(csv(d.timeframe())).append(',')
                    .append(csv(d.period())).append(',')
                    .append(d.decisionTimestamp()).append(',')
                    .append(d.referenceClose()).append(',')
                    .append(d.horizonBars()).append(',')
                    .append(d.thresholdPct()).append(',')
                    .append(d.probabilityPct()).append(',')
                    .append(csv(d.decision())).append(',')
                    .append(d.balancedAccuracyPct()).append(',')
                    .append(d.brierScore()).append(',')
                    .append(d.baselineBrierScore()).append(',')
                    .append(csv(d.status())).append(',')
                    .append(nullable(d.targetTimestamp())).append(',')
                    .append(nullable(d.targetClose())).append(',')
                    .append(nullable(d.forwardReturnPct())).append(',')
                    .append(nullable(d.actualPositive())).append(',')
                    .append(nullable(d.correct())).append(',')
                    .append(csv(d.recordedAt())).append(',')
                    .append(csv(d.resolvedAt()))
                    .append('\n');
        }

        Files.writeString(target, out.toString());
    }

    private boolean resolveExisting(
            AnalysisResult analysis
    ) {
        List<Candle> candles = analysis.candles();

        if (candles == null || candles.isEmpty()) {
            return false;
        }

        boolean changed = false;
        List<MlDecisionRecord> updated =
                new ArrayList<>(state.decisions.size());

        for (MlDecisionRecord decision : state.decisions) {
            if (!decision.pending()
                    || !sameSeries(decision, analysis)) {
                updated.add(decision);
                continue;
            }

            int origin = indexOfTimestamp(
                    candles,
                    decision.decisionTimestamp()
            );

            int target;

            if (origin >= 0) {
                target = origin + decision.horizonBars();
            } else {
                // Si el histórico es una ventana móvil y la vela original
                // ya salió del rango, usamos las velas posteriores al timestamp.
                int firstAfter = firstIndexAfter(
                        candles,
                        decision.decisionTimestamp()
                );
                target = firstAfter < 0
                        ? -1
                        : firstAfter + decision.horizonBars() - 1;
            }

            int lastClosedIndex = candles.size() - 2;

            // Mantiene la misma convención del motor ML: nunca resolver
            // usando la vela potencialmente todavía abierta.
            if (target < 0 || target > lastClosedIndex) {
                updated.add(decision);
                continue;
            }

            Candle targetCandle = candles.get(target);
            double targetClose = targetCandle.close();
            double forwardReturnPct =
                    (targetClose / decision.referenceClose() - 1.0)
                            * 100.0;

            boolean actualPositive =
                    forwardReturnPct > decision.thresholdPct();

            Boolean correct = switch (decision.decision()) {
                case "FAVORABLE" -> actualPositive;
                case "NO OPERAR" -> !actualPositive;
                default -> null;
            };

            updated.add(
                    new MlDecisionRecord(
                            decision.id(),
                            decision.key(),
                            decision.symbol(),
                            decision.marketType(),
                            decision.source(),
                            decision.currency(),
                            decision.timeframe(),
                            decision.period(),
                            decision.decisionTimestamp(),
                            decision.referenceClose(),
                            decision.horizonBars(),
                            decision.thresholdPct(),
                            decision.probabilityPct(),
                            decision.decision(),
                            decision.balancedAccuracyPct(),
                            decision.brierScore(),
                            decision.baselineBrierScore(),
                            "RESOLVED",
                            targetCandle.timestamp(),
                            targetClose,
                            forwardReturnPct,
                            actualPositive,
                            correct,
                            decision.recordedAt(),
                            Instant.now().toString()
                    )
            );
            changed = true;
        }

        if (changed) {
            state.decisions = updated;
        }

        return changed;
    }

    private boolean sameSeries(
            MlDecisionRecord decision,
            AnalysisResult analysis
    ) {
        return equalsIgnoreCase(
                decision.symbol(),
                analysis.symbol()
        )
                && equalsIgnoreCase(
                decision.marketType(),
                analysis.marketType()
        )
                && equalsIgnoreCase(
                decision.source(),
                analysis.source()
        )
                && equalsIgnoreCase(
                decision.timeframe(),
                analysis.timeframe()
        );
    }

    private int indexOfTimestamp(
            List<Candle> candles,
            long timestamp
    ) {
        for (int i = 0; i < candles.size(); i++) {
            if (candles.get(i).timestamp() == timestamp) {
                return i;
            }
        }

        return -1;
    }

    private int firstIndexAfter(
            List<Candle> candles,
            long timestamp
    ) {
        for (int i = 0; i < candles.size(); i++) {
            if (candles.get(i).timestamp() > timestamp) {
                return i;
            }
        }

        return -1;
    }

    private String decisionKey(
            AnalysisResult analysis,
            long timestamp
    ) {
        return String.join(
                "|",
                safe(analysis.marketType()),
                safe(analysis.source()),
                safe(analysis.symbol()),
                safe(analysis.timeframe()),
                String.valueOf(timestamp)
        ).toLowerCase();
    }

    private void trim() {
        if (state.decisions.size() <= MAX_DECISIONS) {
            return;
        }

        state.decisions = state.decisions.stream()
                .sorted(
                        Comparator.comparingLong(
                                MlDecisionRecord::decisionTimestamp
                        ).reversed()
                )
                .limit(MAX_DECISIONS)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private MlDecisionState load() {
        if (!Files.exists(storage)) {
            return new MlDecisionState();
        }

        try {
            MlDecisionState loaded =
                    mapper.readValue(
                            storage.toFile(),
                            MlDecisionState.class
                    );

            if (loaded.decisions == null) {
                loaded.decisions = new ArrayList<>();
            }

            return loaded;
        } catch (Exception ex) {
            return new MlDecisionState();
        }
    }

    private void saveUnchecked() {
        try {
            save();
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "No se pudo guardar la memoria ML: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    private void save() throws IOException {
        Files.createDirectories(storage.getParent());

        Path temp = storage.resolveSibling(
                storage.getFileName() + ".tmp"
        );
        Path backup = storage.resolveSibling(
                storage.getFileName() + ".bak"
        );

        mapper.writeValue(temp.toFile(), state);

        if (Files.exists(storage)) {
            Files.copy(
                    storage,
                    backup,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        try {
            Files.move(
                    temp,
                    storage,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (Exception ex) {
            Files.move(
                    temp,
                    storage,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private double pct(int value, int total) {
        return total == 0
                ? 0.0
                : value * 100.0 / total;
    }

    private boolean equalsIgnoreCase(
            String a,
            String b
    ) {
        return a != null
                && b != null
                && a.equalsIgnoreCase(b);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String nullable(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
