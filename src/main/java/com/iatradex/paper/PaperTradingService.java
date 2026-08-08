package com.iatradex.paper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PaperTradingService {

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final Path storage = Path.of(
            System.getProperty("user.home"),
            ".ia-tradex",
            "paper-trading.json"
    );

    private PaperTradingState state;

    public PaperTradingService() {
        state = load();
    }

    public synchronized PaperTradingState state() {
        return state;
    }

    public synchronized PaperAccount account(String currency) {
        return "ARS".equalsIgnoreCase(currency)
                ? state.ars
                : state.usd;
    }

    public synchronized void resetAccount(
            String currency,
            double initialCapital
    ) throws IOException {
        if (!Double.isFinite(initialCapital) || initialCapital <= 0.0) {
            throw new IllegalArgumentException(
                    "El capital inicial debe ser mayor que cero."
            );
        }

        boolean hasOpen = state.positions.stream()
                .anyMatch(p -> currency.equalsIgnoreCase(p.currency()));

        if (hasOpen) {
            throw new IllegalStateException(
                    "No podés reiniciar esa cuenta mientras tenga posiciones abiertas."
            );
        }

        PaperAccount account = new PaperAccount(
                currency.toUpperCase(),
                initialCapital,
                initialCapital
        );

        if ("ARS".equalsIgnoreCase(currency)) {
            state.ars = account;
        } else {
            state.usd = account;
        }

        save();
    }

    public synchronized PaperPosition buy(
            String symbol,
            String market,
            String currency,
            double quantity,
            double price,
            Double stopLoss,
            Double takeProfit,
            String strategyContext,
            String regimeContext
    ) throws IOException {
        if (!Double.isFinite(quantity) || quantity <= 0.0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        if (!Double.isFinite(price) || price <= 0.0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }

        validateLevels(price, stopLoss, takeProfit);

        PaperAccount account = account(currency);
        double cost = quantity * price;

        if (cost > account.cash() + 0.0000001) {
            throw new IllegalStateException(
                    "Saldo insuficiente en la cuenta " + currency + "."
            );
        }

        PaperPosition position = new PaperPosition(
                UUID.randomUUID().toString(),
                symbol,
                market,
                currency,
                quantity,
                price,
                price,
                stopLoss,
                takeProfit,
                strategyContext == null ? "Manual" : strategyContext,
                regimeContext == null ? "Sin análisis" : regimeContext,
                Instant.now().toString()
        );

        state.positions.add(position);
        setCash(currency, account.cash() - cost);
        save();

        return position;
    }

    public synchronized PaperClosedTrade close(
            String positionId,
            double exitPrice,
            String reason
    ) throws IOException {
        PaperPosition position = state.positions.stream()
                .filter(p -> p.id().equals(positionId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La posición ya no existe."
                        )
                );

        if (!Double.isFinite(exitPrice) || exitPrice <= 0.0) {
            throw new IllegalArgumentException(
                    "El precio de salida debe ser mayor que cero."
            );
        }

        double pnl = position.quantity()
                * (exitPrice - position.entryPrice());

        double pnlPct = position.entryPrice() == 0.0
                ? 0.0
                : (exitPrice / position.entryPrice() - 1.0) * 100.0;

        PaperClosedTrade closed = new PaperClosedTrade(
                UUID.randomUUID().toString(),
                position.symbol(),
                position.market(),
                position.currency(),
                position.quantity(),
                position.entryPrice(),
                exitPrice,
                pnl,
                pnlPct,
                position.strategyContext(),
                position.regimeContext(),
                position.openedAt(),
                Instant.now().toString(),
                reason == null ? "Cierre manual" : reason
        );

        PaperAccount account = account(position.currency());
        setCash(
                position.currency(),
                account.cash() + position.quantity() * exitPrice
        );

        state.positions.removeIf(
                p -> p.id().equals(positionId)
        );
        state.history.add(0, closed);
        save();

        return closed;
    }

    public synchronized void updateMarketPrice(
            String symbol,
            String currency,
            double price
    ) throws IOException {
        if (!Double.isFinite(price) || price <= 0.0) {
            return;
        }

        List<PaperPosition> updated = new ArrayList<>();

        for (PaperPosition position : state.positions) {
            if (position.symbol().equalsIgnoreCase(symbol)
                    && position.currency().equalsIgnoreCase(currency)) {
                updated.add(position.withLastPrice(price));
            } else {
                updated.add(position);
            }
        }

        state.positions = updated;
        save();
    }

    public synchronized double equity(String currency) {
        PaperAccount account = account(currency);
        double positionsValue = state.positions.stream()
                .filter(p -> currency.equalsIgnoreCase(p.currency()))
                .mapToDouble(PaperPosition::marketValue)
                .sum();

        return account.cash() + positionsValue;
    }

    public synchronized double unrealizedPnl(String currency) {
        return state.positions.stream()
                .filter(p -> currency.equalsIgnoreCase(p.currency()))
                .mapToDouble(PaperPosition::unrealizedPnl)
                .sum();
    }

    private void setCash(String currency, double cash) {
        if ("ARS".equalsIgnoreCase(currency)) {
            state.ars = state.ars.withCash(cash);
        } else {
            state.usd = state.usd.withCash(cash);
        }
    }

    private void validateLevels(
            double price,
            Double stopLoss,
            Double takeProfit
    ) {
        if (stopLoss != null && stopLoss > 0.0 && stopLoss >= price) {
            throw new IllegalArgumentException(
                    "En una compra long, el Stop Loss debe quedar debajo del precio de entrada."
            );
        }

        if (takeProfit != null && takeProfit > 0.0 && takeProfit <= price) {
            throw new IllegalArgumentException(
                    "En una compra long, el Take Profit debe quedar por encima del precio de entrada."
            );
        }
    }

    private PaperTradingState load() {
        try {
            if (!Files.exists(storage)) {
                return new PaperTradingState();
            }

            PaperTradingState loaded = mapper.readValue(
                    storage.toFile(),
                    PaperTradingState.class
            );

            if (loaded.ars == null) {
                loaded.ars = new PaperAccount(
                        "ARS",
                        1_000_000.0,
                        1_000_000.0
                );
            }

            if (loaded.usd == null) {
                loaded.usd = new PaperAccount(
                        "USD",
                        10_000.0,
                        10_000.0
                );
            }

            if (loaded.positions == null) {
                loaded.positions = new ArrayList<>();
            }

            if (loaded.history == null) {
                loaded.history = new ArrayList<>();
            }

            return loaded;
        } catch (Exception ignored) {
            return new PaperTradingState();
        }
    }

    private void save() throws IOException {
        Files.createDirectories(storage.getParent());
        mapper.writeValue(storage.toFile(), state);
    }
}
