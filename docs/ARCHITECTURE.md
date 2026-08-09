# Arquitectura de IA-TradeX v2.2.0

IA-TradeX es una aplicación de escritorio 100% Java.

## Capas principales

### Mercado

`com.iatradex.market`

Responsable de obtener precios, históricos, cotizaciones y búsquedas de activos.

Proveedores actuales:

- Binance;
- Kraken;
- Yahoo Finance;
- Open BYMADATA.

### Análisis

`com.iatradex.analysis`

Contiene indicadores, clasificación del régimen de mercado, señales de estrategia y backtesting.

Principios:

- no-lookahead;
- señal sobre vela previa;
- ejecución posterior;
- costos/slippage;
- comparación contra Buy & Hold;
- posibilidad válida de no operar.

### Scanner

`com.iatradex.scanner`

Administra watchlists persistentes, escaneo de activos y Score técnico explicable.

El Score ordena candidatos; no representa probabilidad de ganancia.

### Paper Trading

`com.iatradex.paper`

Incluye:

- cuentas ARS/USD;
- operaciones manuales;
- posiciones abiertas;
- Stop Loss / Take Profit;
- AUTO por activo;
- Cartera AUTO multi-activo;
- límites de riesgo;
- exposición;
- ranking continuo;
- historial;
- Performance;
- persistencia local.

### UI

JavaFX.

La interfaz se mantiene desacoplada de la lógica de mercado y análisis mediante servicios y modelos.

## Persistencia local

Directorio:

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

La escritura utiliza archivo temporal y reemplazo atómico cuando el sistema operativo lo permite.

## Seguridad operativa

IA-TradeX v1.0.0 no contiene integración para enviar órdenes reales.

Toda función denominada Paper Trading, AUTO o Cartera AUTO opera únicamente sobre cuentas simuladas locales.

## Packaging

Entrada Java:

```text
com.iatradex.Launcher
```

Maven artifact:

```text
com.iatradex:ia-tradex:1.0.0
```

macOS:

```text
./package-macos.sh
./package-macos-dmg.sh
```


### Validación

`com.iatradex.validation`

Responsable de:

- Walk-Forward;
- Out-of-Sample;
- robustez temporal;
- optimización controlada;
- exportación CSV de validaciones.

Cada slice temporal se copia a nuevos objetos `Candle` y vuelve a pasar por `IndicatorEngine`, evitando reutilizar indicadores calculados sobre la serie completa.

La búsqueda de parámetros de v1.4 se limita a Riesgo/Stop/Take y nunca utiliza el tramo OOS para seleccionar la configuración.


### Machine Learning

`com.iatradex.ml`

La v2.0 implementa una regresión logística directamente en Java.

Componentes:

- `MlEngine`: construcción del dataset, split temporal, normalización Train-only, entrenamiento, evaluación e inferencia;
- `LogisticRegressionModel`: cálculo probabilístico con pesos normalizados;
- `MlReport`: métricas OOS y decisión;
- `MlFeature`: explicabilidad por peso.

La etiqueta usa un horizonte fijo de 5 velas y un retorno futuro superior a 0,5%.

Las muestras de entrenamiento cuyo horizonte de etiqueta invade OOS se descartan para evitar leakage.

La capa ML permanece desacoplada de la ejecución automática.


### Integración ML con Scanner

`ScannerEngine` acepta un `MlFilterMode`:

- `DISABLED`
- `INFORMATIVE`
- `CONFIRMATION`

El análisis de mercado se ejecuta una sola vez por activo. Si ML está activo, `MlEngine` trabaja sobre el mismo `AnalysisResult`, evitando una segunda descarga de datos.

`ScannerResult` conserva separadamente:

- señal técnica;
- Score técnico;
- decisión ML;
- probabilidad ML;
- decisión final;
- explicación ML.

La separación impide mezclar semánticamente el Score de reglas con la probabilidad estadística.

### Integración ML con Cartera AUTO

`PaperPortfolioAutoConfig` persiste `MlFilterMode`.

En modo `CONFIRMATION`, `PaperPortfolioAutoEngine` requiere `mlDecision = FAVORABLE` antes de continuar con position sizing, límites y riesgo.

El filtro ML se aplica solo a entradas. La gestión de posiciones existentes conserva Stop Loss, Take Profit y salidas por estrategia.


### Memoria ML

`MlDecisionService` persiste decisiones generadas por el modelo y las resuelve cuando existen suficientes velas posteriores.

Componentes:

- `MlDecisionRecord`: observación individual;
- `MlDecisionState`: contenedor persistente;
- `MlDecisionStats`: agregados de seguimiento;
- `MlDecisionService`: deduplicación, resolución, estadísticas, CSV y persistencia atómica.

Clave de deduplicación:

```text
marketType | source | symbol | timeframe | decisionTimestamp
```

La resolución usa únicamente velas cerradas y soporta ventanas históricas móviles buscando la primera vela posterior al timestamp original cuando la vela de decisión ya no está incluida.

La memoria ML no se utiliza todavía como mecanismo de autoajuste del modelo.
