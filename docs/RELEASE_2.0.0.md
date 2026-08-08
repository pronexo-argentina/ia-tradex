# IA-TradeX 2.0.0

IA-TradeX 2.0.0 incorpora la primera capa de Machine Learning del proyecto.

## Modelo

Regresión logística regularizada implementada 100% en Java.

## Objetivo

Clasificar si el cierre dentro de cinco velas supera al cierre actual en más de 0,5%.

## Variables

Retornos recientes, EMA, distancia a media, RSI, ATR, rango y volumen relativo.

## Evaluación

El modelo se entrena con el tramo inicial y se evalúa sobre un período OOS cronológicamente posterior.

La normalización se obtiene solo desde entrenamiento y se eliminan muestras cuya etiqueta futura atraviese la frontera OOS.

## Salida

- FAVORABLE
- OBSERVAR
- NO OPERAR

La probabilidad mostrada pertenece a la etiqueta estadística aprendida y no equivale a probabilidad real de beneficio.

## Alcance

En 2.0 el ML es una capa de investigación y explicabilidad. No ejecuta operaciones ni modifica automáticamente Scanner o Cartera AUTO.

Esto permite acumular evidencia OOS antes de usar un modelo como filtro de Paper Trading.
