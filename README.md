# IA-TradeX

**IA-TradeX** es una aplicación de escritorio 100% Java para análisis técnico, backtesting, Scanner, Paper Trading y gestión automatizada de una cartera simulada.

La v2.0.0 agrega una primera capa de Machine Learning 100% Java sobre la base validada de la v1.4. No envía órdenes reales y no requiere Python.

![Java](https://img.shields.io/badge/Java-21-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-23-blue)
![License](https://img.shields.io/badge/License-AGPL--3.0-green)
![Version](https://img.shields.io/badge/version-2.0.0-brightgreen)

## Qué incluye IA-TradeX 2.0.0

- análisis de **Criptomonedas, Argentina e Internacional**;
- Binance y Kraken para Crypto;
- Yahoo Finance para Internacional;
- Open BYMADATA para Argentina;
- indicadores EMA 12/26, RSI 14 y ATR 14;
- detección de régimen de mercado;
- estrategias EMA Cross, Momentum, Mean Reversion y Breakout;
- backtesting sin look-ahead con comisión y slippage simulados;
- comparación contra Buy & Hold;
- Watchlists persistentes;
- Scanner manual y automático;
- Score técnico explicable de 0 a 100;
- Paper Trading manual con cuentas ARS y USD;
- Stop Loss y Take Profit automáticos;
- AUTO por activo;
- Cartera AUTO multi-activo;
- límites globales y por mercado;
- control de riesgo y exposición;
- ranking continuo de candidatos;
- Dashboard de Performance;
- estadísticas por estrategia, mercado y régimen;
- exportación de operaciones y estadísticas a CSV;
- persistencia local con backup `.bak`;
- packaging macOS `.app` y `.dmg`;
- menú nativo de macOS;
- licencia GNU AGPL v3.0.

> IA-TradeX es una herramienta de investigación y simulación. Los resultados históricos no garantizan resultados futuros y no constituyen asesoramiento financiero.

## Capturas

### Dashboard · Criptomonedas

![Dashboard Crypto](https://raw.githubusercontent.com/pronexo-argentina/ia-tradex/main/docs/images/dashboard-crypto.png)

### Dashboard · Argentina

![Dashboard Argentina](https://raw.githubusercontent.com/pronexo-argentina/ia-tradex/main/docs/images/dashboard-argentina.png)

### Análisis técnico · Argentina

![Análisis Argentina](https://raw.githubusercontent.com/pronexo-argentina/ia-tradex/main/docs/images/analysis-argentina.png)

### Análisis técnico · Crypto

![Análisis Crypto](https://raw.githubusercontent.com/pronexo-argentina/ia-tradex/main/docs/images/analysis-crypto.png)

### Paper Trading

![Paper Trading](https://raw.githubusercontent.com/pronexo-argentina/ia-tradex/main/docs/images/paper-trading.png)

## Mercados y fuentes

| Mercado | Fuente | Moneda | Estado |
|---|---|---:|---|
| Criptomonedas | Binance / Kraken | USD/USDT | Activo |
| Argentina | Open BYMADATA | ARS | Activo |
| Internacional | Yahoo Finance | USD/original | Activo |

Los datos públicos de Open BYMADATA pueden tener aproximadamente 20 minutos de demora.

## Análisis y backtesting

IA-TradeX calcula:

- EMA 12;
- EMA 26;
- RSI 14;
- ATR 14;
- tendencia;
- volatilidad;
- régimen;
- señal técnica;
- métricas históricas.

El backtest usa la señal de una vela cerrada y ejecuta en la apertura siguiente. Incluye comisión y slippage simulados para evitar un resultado artificialmente favorable.

Métricas principales:

- capital final;
- retorno;
- Buy & Hold;
- drawdown máximo;
- Win Rate;
- Profit Factor;
- Sharpe;
- cantidad de operaciones;
- ganancia y pérdida media.

## Estrategias

### EMA Cross
Cruce de EMA rápida y lenta.

### Momentum
Busca continuidad de impulso en un contexto compatible.

### Mean Reversion
Busca posibles retornos hacia la media después de una desviación.

### Breakout
Busca ruptura de máximos recientes.

La etiqueta **MEJOR HISTÓRICO** identifica solamente la estrategia con mejor resultado dentro del período analizado. No es una recomendación futura.

## Régimen de mercado

El motor clasifica el mercado como:

- `ALCISTA`;
- `BAJISTA`;
- `LATERAL`.

También estima:

- fuerza `BAJA / MEDIA / ALTA`;
- volatilidad `BAJA / MEDIA / ALTA`.

Las estrategias compatibles se muestran según reglas explicables. Esta parte no usa Machine Learning.

## Watchlist y Scanner

El Scanner permite crear listas separadas para:

- Argentina;
- Internacional;
- Crypto.

Cada activo puede mostrar:

- régimen;
- volatilidad;
- RSI;
- estrategia seleccionada;
- señal;
- Score;
- explicación.

El **Score 0–100 no representa una probabilidad de ganar**. Es un ranking técnico basado en tendencia, fuerza, RSI, compatibilidad de estrategia, señal, retorno histórico y volatilidad.

El Scanner puede ejecutarse manualmente o cada 60 segundos mientras su ventana permanece abierta.

## Paper Trading

Paper Trading utiliza cuentas simuladas independientes:

- **ARS** para Argentina;
- **USD** para Internacional y Crypto.

Permite:

- configurar capital inicial;
- comprar manualmente el activo analizado;
- cerrar posiciones;
- Stop Loss y Take Profit;
- actualizar precios;
- consultar equity y P&L;
- conservar historial.

Estado local:

```text
~/.ia-tradex/paper-trading.json
```

## AUTO por activo

El modo AUTO puede administrar un activo con:

- estrategia;
- capital máximo;
- riesgo por operación;
- Stop Loss;
- Take Profit.

Las señales automáticas utilizan la última vela cerrada.

## Cartera AUTO multi-activo

Cartera AUTO utiliza las watchlists y el Scanner para trabajar con varios activos simultáneamente.

Controles:

- Score mínimo;
- máximo total de posiciones;
- máximo para Argentina;
- máximo para Internacional;
- máximo para Crypto;
- riesgo global máximo;
- riesgo por operación;
- capital máximo por operación;
- Stop Loss;
- Take Profit.

Antes de abrir una posición exige:

1. señal `ENTRADA`;
2. Score suficiente;
3. posición no duplicada;
4. espacio en el límite total;
5. espacio en el límite del mercado;
6. efectivo disponible;
7. margen de riesgo global.

El panel muestra exposición y riesgo separados para ARS y USD.

## Performance

El botón **Performance** analiza el historial real de Paper Trading.

Métricas generales por cuenta:

- Equity;
- retorno desde el capital inicial;
- P&L realizado;
- P&L no realizado;
- Win Rate;
- Profit Factor;
- drawdown realizado;
- operaciones cerradas.

También agrupa resultados por:

- estrategia;
- mercado;
- régimen.

Cada agrupación muestra operaciones, Win Rate, P&L, P&L medio %, Profit Factor, drawdown realizado, mejor operación y peor operación.

Cuando existe un análisis actual en la misma moneda, Performance muestra además la comparación histórica entre la estrategia seleccionada y **Buy & Hold**.

## Exportación CSV

Desde Performance se puede exportar:

- **Operaciones CSV:** historial completo de operaciones cerradas.
- **Estadísticas CSV:** resumen y agrupaciones por estrategia, mercado y régimen.

## Persistencia y backups

IA-TradeX guarda datos localmente en:

```text
~/.ia-tradex/
```

Archivos principales:

```text
paper-trading.json
paper-trading.json.bak
watchlists.json
watchlists.json.bak
```

Las escrituras utilizan un archivo temporal y reemplazo atómico cuando el sistema operativo lo permite.

## Interfaz macOS

En macOS:

- el icono de la aplicación y del Dock usa solamente el isotipo IX;
- `Acerca de IA-TradeX` se integra en el menú nativo de la aplicación;
- la barra interna queda reservada para las funciones de trading/análisis.

## Requisitos de desarrollo

- Java 21;
- Maven;
- macOS, Linux o Windows para ejecución JavaFX;
- `jpackage` para packaging macOS.

## Ejecutar en desarrollo

```bash
mvn clean javafx:run
```

Validación rápida:

```bash
./verify-project.sh
```

## Empaquetar para macOS

Aplicación `.app`:

```bash
./package-macos.sh
```

Resultado:

```text
dist/IA-TradeX.app
```

DMG:

```bash
./package-macos-dmg.sh
```

## Arquitectura

Paquetes principales:

```text
com.iatradex.market
com.iatradex.analysis
com.iatradex.scanner
com.iatradex.paper
com.iatradex.ui
```

Más detalle en [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Documentación

- [Manual de usuario](docs/MANUAL.md)
- [Arquitectura](docs/ARCHITECTURE.md)
- [QA v1.0.0](docs/QA.md)
- [Notas de release 1.0.0](docs/RELEASE_1.0.0.md)
- [Notas de release 1.4.0](docs/RELEASE_1.4.0.md)
- [Notas de release 2.0.0](docs/RELEASE_2.0.0.md)
- [Changelog](docs/CHANGELOG.md)


## Laboratorio de Validación · v1.1 a v1.4

El botón **Validación** trabaja sobre el activo actualmente analizado y ejecuta cuatro capas.

### v1.1 · Walk-Forward

El histórico se divide cronológicamente en varios bloques.

En cada fold:

1. se usa únicamente el tramo pasado como entrenamiento;
2. se selecciona una combinación acotada de Riesgo / Stop / Take dentro de ese tramo;
3. esos parámetros se congelan;
4. se evalúan sobre el bloque temporal siguiente.

Los indicadores se recalculan de forma independiente para evitar reutilizar valores calculados con información futura.

La tabla muestra el retorno medio Walk-Forward y cuántos folds terminaron positivos.

### v1.2 · Out-of-Sample

Aproximadamente el 70% inicial se usa como In-Sample y el 30% final queda reservado como Out-of-Sample.

El tramo OOS no participa en la elección de parámetros de la optimización.

Se comparan:

- retorno In-Sample;
- retorno OOS;
- Buy & Hold OOS.

### v1.3 · Robustez temporal

IA-TradeX combina la evidencia OOS y Walk-Forward en un **Score de Robustez 0–100** y clasifica cada estrategia:

- `ROBUSTA`;
- `DUDOSA`;
- `SOBREAJUSTADA`.

Este score tampoco es una probabilidad de ganancia. Resume consistencia temporal dentro del activo y período analizados.

La v1.3 evalúa robustez **temporal** del activo actual. La validación cruzada masiva entre muchos activos/mercados queda como una etapa posterior.

### v1.4 · Optimización controlada

Se prueba una grilla pequeña y explícita:

```text
Riesgo: 0,5% / 1,0% / 1,5%
Stop:   1,5% / 2,0% / 2,5% / 3,0%
Take:   3,0% / 4,0% / 5,0% / 6,0%
```

La selección penaliza drawdown y configuraciones con muy pocas operaciones.

Después de seleccionar la mejor configuración **solo con entrenamiento**, se prueba sin cambios sobre OOS.

La optimización no modifica todavía períodos de EMA, RSI ni reglas internas de las estrategias. Eso reduce el espacio de búsqueda y el riesgo de curve fitting.

### Requisito de datos

El laboratorio exige al menos **80 velas**. Si el período seleccionado es demasiado corto, IA-TradeX pide elegir un histórico mayor.

### Exportación

La validación puede exportarse a CSV con:

- resultados In-Sample;
- OOS;
- Buy & Hold OOS;
- Walk-Forward;
- Score de Robustez;
- clasificación;
- parámetros optimizados;
- resultado de esos parámetros en entrenamiento y OOS.



## IA / Machine Learning · v2.0

El botón **IA / ML** entrena y evalúa un modelo de regresión logística 100% Java sobre el activo actualmente analizado.

El objetivo no es adivinar el precio exacto. El modelo clasifica si el contexto actual es compatible con un movimiento favorable dentro de un horizonte fijo.

### Etiqueta de entrenamiento

En v2.0 la etiqueta es deliberadamente fija:

```text
Horizonte: 5 velas
Clase positiva: cierre futuro > cierre actual + 0,5%
```

No se optimiza esa definición contra el OOS.

### Variables utilizadas

- retorno de 1 vela;
- retorno de 5 velas;
- retorno de 20 velas;
- separación EMA 12/26;
- distancia precio / EMA 26;
- RSI normalizado;
- ATR como porcentaje del precio;
- rango de vela;
- volumen relativo frente a 20 velas.

### Separación temporal

El modelo usa aproximadamente el 70% inicial como entrenamiento y el tramo final como OOS.

La separación es estricta: una muestra de entrenamiento se descarta si su etiqueta a 5 velas invade el período OOS.

Además:

- los indicadores son causales;
- la normalización se calcula solo con entrenamiento;
- los pesos del modelo se ajustan solo con entrenamiento;
- OOS se usa únicamente para evaluar.

### Métricas del modelo

La pantalla muestra:

- probabilidad producida por el modelo;
- Balanced Accuracy;
- Precision;
- Recall;
- Brier Score;
- Brier Score del baseline;
- cantidad de muestras Train / OOS.

El **Brier Score** mide error probabilístico: cuanto menor, mejor.

El baseline utiliza la frecuencia histórica de la clase positiva observada en entrenamiento.

### Decisión

La capa ML puede devolver:

```text
FAVORABLE
OBSERVAR
NO OPERAR
```

`FAVORABLE` requiere una probabilidad alta y que el Brier OOS supere al baseline.

`NO OPERAR` es una salida válida del modelo.

La probabilidad mostrada es una probabilidad interna de la etiqueta aprendida. **No representa la probabilidad real de ganar una operación.**

### Explicabilidad

IA-TradeX muestra los pesos estandarizados de las variables y una importancia relativa basada en el valor absoluto de cada peso.

Esto permite inspeccionar qué variables están influyendo más en la clasificación.

### Uso en v2.0

La capa ML es **informativa y de investigación**. No modifica automáticamente las reglas de Paper Trading, Cartera AUTO ni Scanner.

La integración del ML como filtro de ejecución debería hacerse solamente después de acumular evidencia suficiente de calidad OOS.

### Exportación

El reporte ML puede exportarse a CSV con métricas, decisión y variables del modelo.


## Próximas etapas

La v2.0.0 incorpora el primer modelo ML, pero lo mantiene desacoplado de la ejecución automática.

Siguientes candidatos:

- validación cruzada del modelo en múltiples activos;
- calibración probabilística;
- comparación contra modelos más simples y árboles;
- persistencia/versionado de modelos;
- ML como filtro opcional del Scanner;
- ML como filtro opcional de Cartera AUTO, inicialmente solo en Paper Trading;
- Monte Carlo / bootstrap;
- WebSocket/streaming donde la fuente lo permita;
- sentimiento/noticias;
- explicabilidad avanzada;
- integración opcional con brokers en una etapa posterior.

## Acerca de

**Juan Manuel De Castro**  
**Email:** jm@pronexo.com  
**Web:** https://www.pronexo.com

## Licencia

IA-TradeX se distribuye bajo **GNU Affero General Public License v3.0 (AGPL-3.0)**.

Consulta [LICENSE](LICENSE) para el texto completo.
