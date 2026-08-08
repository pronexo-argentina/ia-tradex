# IA-TradeX 1.4.0

IA-TradeX 1.4.0 agrega el laboratorio de validación previo a Machine Learning.

## 1.1 · Walk-Forward

Selección de Riesgo/Stop/Take sobre tramos de entrenamiento y evaluación posterior con parámetros congelados.

## 1.2 · Out-of-Sample

Reserva cronológica aproximada del 30% final para comprobar si el comportamiento se sostiene fuera del período de selección.

## 1.3 · Robustez

Score 0–100 y clasificación ROBUSTA / DUDOSA / SOBREAJUSTADA.

## 1.4 · Optimización controlada

Grilla acotada de riesgo y gestión de salida, penalizando drawdown y muestras con pocas operaciones.

El OOS nunca se utiliza para elegir la configuración.

## Alcance

La robustez implementada es principalmente temporal sobre el activo actualmente analizado. La validación cruzada masiva entre numerosos activos y mercados queda para una etapa posterior.

IA-TradeX sigue siendo una herramienta de investigación y Paper Trading. No envía órdenes reales.
