package com.iatradex.paper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PaperPerformanceAnalyzer {

    public PaperPerformanceSummary summary(
            PaperTradingService service,
            String currency
    ) {
        List<PaperClosedTrade> trades = service.state().history.stream()
                .filter(t -> currency.equalsIgnoreCase(t.currency()))
                .sorted(Comparator.comparing(
                        PaperClosedTrade::closedAt,
                        Comparator.nullsLast(String::compareTo)
                ))
                .toList();

        double realized = trades.stream()
                .mapToDouble(PaperClosedTrade::pnl)
                .sum();

        int wins = (int) trades.stream()
                .filter(t -> t.pnl() > 0.0)
                .count();

        int losses = (int) trades.stream()
                .filter(t -> t.pnl() < 0.0)
                .count();

        double grossProfit = trades.stream()
                .filter(t -> t.pnl() > 0.0)
                .mapToDouble(PaperClosedTrade::pnl)
                .sum();

        double grossLoss = Math.abs(
                trades.stream()
                        .filter(t -> t.pnl() < 0.0)
                        .mapToDouble(PaperClosedTrade::pnl)
                        .sum()
        );

        Double profitFactor = grossLoss == 0.0
                ? (grossProfit > 0.0 ? null : 0.0)
                : grossProfit / grossLoss;

        double initial = service.account(currency).initialCapital();
        double returnPct = initial == 0.0
                ? 0.0
                : (service.equity(currency) / initial - 1.0) * 100.0;

        double winRate = trades.isEmpty()
                ? 0.0
                : wins * 100.0 / trades.size();

        return new PaperPerformanceSummary(
                currency,
                trades.size(),
                wins,
                losses,
                realized,
                service.unrealizedPnl(currency),
                service.equity(currency),
                returnPct,
                winRate,
                profitFactor,
                maxDrawdown(trades),
                trades.stream()
                        .mapToDouble(PaperClosedTrade::pnl)
                        .max()
                        .orElse(0.0),
                trades.stream()
                        .mapToDouble(PaperClosedTrade::pnl)
                        .min()
                        .orElse(0.0)
        );
    }

    public List<PaperPerformanceStat> byStrategy(
            PaperTradingService service,
            String currency
    ) {
        return group(
                service,
                currency,
                t -> clean(t.strategyContext(), "Sin estrategia")
        );
    }

    public List<PaperPerformanceStat> byMarket(
            PaperTradingService service,
            String currency
    ) {
        return group(
                service,
                currency,
                t -> clean(t.market(), "Sin mercado")
        );
    }

    public List<PaperPerformanceStat> byRegime(
            PaperTradingService service,
            String currency
    ) {
        return group(
                service,
                currency,
                t -> clean(t.regimeContext(), "Sin régimen")
        );
    }

    public void exportStatsCsv(
            PaperTradingService service,
            String currency,
            Path target
    ) throws IOException {
        PaperPerformanceSummary summary =
                summary(service, currency);

        StringBuilder out = new StringBuilder();

        out.append("section,group,trades,wins,win_rate_pct,pnl,avg_pnl_pct,profit_factor,max_drawdown,best_trade,worst_trade\n");

        out.append("SUMMARY,")
                .append(csv(currency)).append(',')
                .append(summary.closedTrades()).append(',')
                .append(summary.wins()).append(',')
                .append(summary.winRatePct()).append(',')
                .append(summary.realizedPnl()).append(',')
                .append("").append(',')
                .append(summary.profitFactor() == null
                        ? ""
                        : summary.profitFactor()).append(',')
                .append(summary.maxRealizedDrawdown()).append(',')
                .append(summary.bestTrade()).append(',')
                .append(summary.worstTrade())
                .append('\n');

        appendSection(out, "STRATEGY", byStrategy(service, currency));
        appendSection(out, "MARKET", byMarket(service, currency));
        appendSection(out, "REGIME", byRegime(service, currency));

        Files.writeString(
                target,
                out.toString(),
                StandardCharsets.UTF_8
        );
    }

    private void appendSection(
            StringBuilder out,
            String section,
            List<PaperPerformanceStat> stats
    ) {
        for (PaperPerformanceStat stat : stats) {
            out.append(section).append(',')
                    .append(csv(stat.group())).append(',')
                    .append(stat.trades()).append(',')
                    .append(stat.wins()).append(',')
                    .append(stat.winRatePct()).append(',')
                    .append(stat.pnl()).append(',')
                    .append(stat.avgPnlPct()).append(',')
                    .append(stat.profitFactor() == null
                            ? ""
                            : stat.profitFactor()).append(',')
                    .append(stat.maxRealizedDrawdown()).append(',')
                    .append(stat.bestTrade()).append(',')
                    .append(stat.worstTrade())
                    .append('\n');
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private List<PaperPerformanceStat> group(
            PaperTradingService service,
            String currency,
            Function<PaperClosedTrade, String> classifier
    ) {
        Map<String, List<PaperClosedTrade>> groups =
                new LinkedHashMap<>();

        service.state().history.stream()
                .filter(t -> currency.equalsIgnoreCase(t.currency()))
                .forEach(t -> groups
                        .computeIfAbsent(
                                classifier.apply(t),
                                key -> new ArrayList<>()
                        )
                        .add(t)
                );

        return groups.entrySet().stream()
                .map(entry -> stat(entry.getKey(), entry.getValue()))
                .sorted(
                        Comparator.comparingDouble(
                                PaperPerformanceStat::pnl
                        ).reversed()
                )
                .toList();
    }

    private PaperPerformanceStat stat(
            String group,
            List<PaperClosedTrade> trades
    ) {
        List<PaperClosedTrade> chronological = trades.stream()
                .sorted(Comparator.comparing(
                        PaperClosedTrade::closedAt,
                        Comparator.nullsLast(String::compareTo)
                ))
                .toList();

        int wins = (int) trades.stream()
                .filter(t -> t.pnl() > 0.0)
                .count();

        double pnl = trades.stream()
                .mapToDouble(PaperClosedTrade::pnl)
                .sum();

        double grossProfit = trades.stream()
                .filter(t -> t.pnl() > 0.0)
                .mapToDouble(PaperClosedTrade::pnl)
                .sum();

        double grossLoss = Math.abs(
                trades.stream()
                        .filter(t -> t.pnl() < 0.0)
                        .mapToDouble(PaperClosedTrade::pnl)
                        .sum()
        );

        Double profitFactor = grossLoss == 0.0
                ? (grossProfit > 0.0 ? null : 0.0)
                : grossProfit / grossLoss;

        double avgPnlPct = trades.stream()
                .mapToDouble(PaperClosedTrade::pnlPct)
                .average()
                .orElse(0.0);

        return new PaperPerformanceStat(
                group,
                trades.size(),
                wins,
                trades.isEmpty() ? 0.0 : wins * 100.0 / trades.size(),
                pnl,
                avgPnlPct,
                profitFactor,
                maxDrawdown(chronological),
                trades.stream()
                        .mapToDouble(PaperClosedTrade::pnl)
                        .max()
                        .orElse(0.0),
                trades.stream()
                        .mapToDouble(PaperClosedTrade::pnl)
                        .min()
                        .orElse(0.0)
        );
    }

    private double maxDrawdown(List<PaperClosedTrade> trades) {
        double cumulative = 0.0;
        double peak = 0.0;
        double maxDrawdown = 0.0;

        for (PaperClosedTrade trade : trades) {
            cumulative += trade.pnl();
            peak = Math.max(peak, cumulative);
            maxDrawdown = Math.max(
                    maxDrawdown,
                    peak - cumulative
            );
        }

        return maxDrawdown;
    }

    private String clean(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value;
    }
}
