package com.iatradex.ui;

import com.iatradex.ml.MlDecisionRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class MlDecisionRow {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.systemDefault());

    private final StringProperty time;
    private final StringProperty symbol;
    private final StringProperty timeframe;
    private final StringProperty decision;
    private final StringProperty probability;
    private final StringProperty status;
    private final StringProperty forwardReturn;
    private final StringProperty actual;
    private final StringProperty correct;

    public MlDecisionRow(MlDecisionRecord record) {
        time = new SimpleStringProperty(
                DATE.format(
                        Instant.ofEpochMilli(
                                record.decisionTimestamp()
                        )
                )
        );
        symbol = new SimpleStringProperty(
                record.symbol()
        );
        timeframe = new SimpleStringProperty(
                record.timeframe()
        );
        decision = new SimpleStringProperty(
                record.decision()
        );
        probability = new SimpleStringProperty(
                String.format(
                        "%.1f%%",
                        record.probabilityPct()
                )
        );
        status = new SimpleStringProperty(
                record.status()
        );
        forwardReturn = new SimpleStringProperty(
                record.forwardReturnPct() == null
                        ? "—"
                        : String.format(
                                "%+.2f%%",
                                record.forwardReturnPct()
                        )
        );
        actual = new SimpleStringProperty(
                record.actualPositive() == null
                        ? "—"
                        : record.actualPositive()
                        ? "POSITIVA"
                        : "NO POSITIVA"
        );
        correct = new SimpleStringProperty(
                record.correct() == null
                        ? "—"
                        : record.correct()
                        ? "SÍ"
                        : "NO"
        );
    }

    public StringProperty timeProperty() { return time; }
    public StringProperty symbolProperty() { return symbol; }
    public StringProperty timeframeProperty() { return timeframe; }
    public StringProperty decisionProperty() { return decision; }
    public StringProperty probabilityProperty() { return probability; }
    public StringProperty statusProperty() { return status; }
    public StringProperty forwardReturnProperty() { return forwardReturn; }
    public StringProperty actualProperty() { return actual; }
    public StringProperty correctProperty() { return correct; }
}
