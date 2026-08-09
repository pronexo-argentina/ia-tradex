# IA-TradeX 2.1.0

IA-TradeX 2.1 conecta por primera vez el modelo Machine Learning con el flujo operativo de Paper Trading.

## Scanner

El Scanner incorpora tres modos:

- Desactivado
- Informativo
- Confirmación

Score técnico y ML permanecen separados.

En Confirmación, una señal técnica ENTRADA queda bloqueada salvo que ML devuelva FAVORABLE.

## Cartera AUTO

Cartera AUTO persiste el mismo modo ML.

En Confirmación, ML actúa como un filtro adicional de entrada antes de position sizing y gestión de riesgo.

Un resultado ML no disponible bloquea conservadoramente la entrada cuando Confirmación está activo.

## Seguridad

La integración continúa limitada a Paper Trading.

ML no controla órdenes reales y no sustituye Stop Loss, Take Profit, límites de posiciones, capital ni riesgo global.

## Compatibilidad

Las configuraciones antiguas sin modo ML se normalizan a Desactivado.

## Documentación

README, Manual y Changelog se actualizan junto con esta release.
