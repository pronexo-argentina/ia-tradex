# IA-TradeX 2.2.0

IA-TradeX 2.2 incorpora memoria persistente de decisiones Machine Learning.

Cada decisión del modelo se registra una sola vez por activo/timeframe/vela y queda pendiente hasta disponer de cinco velas cerradas posteriores.

Al madurar, IA-TradeX calcula el retorno observado, determina si la etiqueta positiva de +0,5% ocurrió y evalúa las decisiones FAVORABLE y NO OPERAR.

La nueva pantalla Memoria ML muestra historial, pendientes, tasa de acierto accionable, tasas separadas por decisión y retorno medio observado.

Los datos se guardan en `~/.ia-tradex/ml-decisions.json` con backup `.bak` y escritura temporal/atómica.

Esta métrica evalúa la clasificación del modelo; no equivale todavía al P&L de una estrategia con Stop Loss, Take Profit, comisiones o slippage.
