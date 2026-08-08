# Manual de usuario · IA-TradeX v2.0.0

Este manual está pensado para una persona sin experiencia previa en trading.

IA-TradeX es una aplicación de análisis y simulación. No envía órdenes reales y los resultados históricos no garantizan resultados futuros.

## 1. Pantalla principal

La barra superior permite elegir:

- **Mercado**
- **Fuente**
- **Activo**
- **Vela**
- **Período**

Acciones principales:

- **Analizar mercado**
- **Scanner**
- **Performance**
- **Paper Trading**

En macOS, **Acerca de IA-TradeX** está en el menú nativo superior de la aplicación.

## 2. Mercados

### Criptomonedas

Fuentes disponibles:

- Binance
- Kraken

Ejemplos:

```text
BTC/USDT
ETH/USDT
```

### Argentina

Fuente:

```text
Open BYMADATA
```

Los precios se muestran en ARS.

Puede incluir acciones argentinas y CEDEARs. Los datos públicos pueden tener aproximadamente 20 minutos de demora.

### Internacional

Fuente:

```text
Yahoo Finance
```

Permite buscar acciones y otros instrumentos por ticker o nombre.

Ejemplos:

```text
AAPL
MSFT
NVDA
ASML
```

## 3. Qué es una vela

Una vela resume el movimiento del precio durante un intervalo.

Incluye:

- Open: apertura
- High: máximo
- Low: mínimo
- Close: cierre
- Volume: volumen

Velas disponibles según el mercado:

```text
1h
4h
1d
```

Argentina utiliza actualmente histórico diario en la interfaz principal.

## 4. Período

El período define cuánto pasado se analiza:

```text
1m  = aproximadamente un mes
3m  = aproximadamente tres meses
6m  = aproximadamente seis meses
1y  = aproximadamente un año
```

## 5. Indicadores

### EMA

EMA significa Exponential Moving Average.

IA-TradeX usa:

- EMA 12
- EMA 26

La EMA 12 reacciona más rápido a cambios recientes que la EMA 26.

### RSI

RSI va de 0 a 100.

Como referencia general:

- arriba de 70: zona alta/sobrecomprada;
- debajo de 30: zona baja/sobrevendida;
- entre 50 y 70: puede acompañar momentum positivo.

No debe utilizarse de forma aislada.

### ATR

ATR mide volatilidad.

Un ATR alto indica movimientos relativamente amplios, pero no dice si el precio va a subir o bajar.

## 6. Régimen de mercado

IA-TradeX clasifica el contexto como:

- `ALCISTA`
- `BAJISTA`
- `LATERAL`

También estima fuerza y volatilidad:

```text
BAJA
MEDIA
ALTA
```

Las estrategias compatibles son una clasificación por reglas. No significa que sean rentables ni recomendadas.

## 7. Estrategias

### EMA Cross

Busca cruces entre EMA 12 y EMA 26.

### Momentum

Busca continuidad de impulso positivo.

### Mean Reversion

Busca posibles retornos hacia la media después de movimientos extremos.

### Breakout

Busca rupturas sobre máximos recientes.

## 8. Backtesting

Un backtest reproduce una estrategia sobre datos históricos.

IA-TradeX intenta evitar el look-ahead bias:

1. la señal se detecta en una vela;
2. la entrada se ejecuta en la apertura siguiente.

También incorpora:

- comisión simulada;
- slippage;
- Stop Loss;
- Take Profit.

Si Stop y Take se alcanzan dentro de la misma vela, el motor utiliza una resolución conservadora.

## 9. Métricas del backtest

### Capital final

Capital al terminar la simulación.

### Retorno

Cambio porcentual frente al capital inicial.

### Buy & Hold

Resultado de comprar el activo al comienzo y mantenerlo hasta el final.

### Drawdown máximo

Mayor caída desde un máximo previo de la curva de capital.

### Win Rate

Porcentaje de operaciones con ganancia.

### Profit Factor

Ganancias brutas divididas por pérdidas brutas.

### Sharpe

Relaciona retorno con variabilidad. Debe interpretarse junto con otras métricas.

### Ganancia / pérdida media

Promedio de operaciones ganadoras y perdedoras.

## 10. Mejor histórico

La etiqueta **MEJOR HISTÓRICO** significa que esa estrategia obtuvo el mejor resultado dentro del período analizado.

No significa:

- que vaya a ganar en el futuro;
- que sea la mejor estrategia para siempre;
- que deba utilizarse con dinero real.

## 11. Scanner

Abrí **Scanner** desde la barra superior.

Las watchlists se separan en:

- Argentina
- Internacional
- Criptomonedas

### Agregar un activo

1. Elegí Mercado.
2. Elegí Fuente.
3. Elegí Vela.
4. Elegí Período.
5. Escribí el ticker/símbolo.
6. Presioná **Agregar**.

### Quitar un activo

Seleccioná el activo de la watchlist y presioná **Quitar**.

### Escanear

Presioná **Escanear**.

El Scanner analiza los activos en segundo plano para no bloquear JavaFX.

Un error en un símbolo no cancela el resto.

### Auto 60 s

Activa un nuevo escaneo cada 60 segundos mientras la ventana Scanner permanezca abierta.

## 12. Score del Scanner

El Score va de 0 a 100.

Puede considerar:

- tendencia;
- fuerza;
- RSI;
- compatibilidad de estrategia;
- señal;
- retorno histórico;
- volatilidad.

**No es una probabilidad de ganancia.**

Ejemplo conceptual:

```text
YPFD · Score 84 · ENTRADA · Momentum
GGAL · Score 56 · ESPERAR · Mean Reversion
```

Desde un resultado podés:

- abrirlo en el dashboard;
- enviarlo a Paper Trading AUTO.

## 13. Watchlists

Persistencia:

```text
~/.ia-tradex/watchlists.json
```

Backup:

```text
~/.ia-tradex/watchlists.json.bak
```

## 14. Paper Trading

Paper Trading permite operar con dinero simulado.

Cuentas:

- ARS
- USD

### Capital inicial

Puede modificarse mientras no existan posiciones abiertas en esa cuenta.

### Comprar manualmente

1. Analizá un activo.
2. Abrí **Paper Trading**.
3. Elegí ARS o USD.
4. Presioná **Comprar activo actual**.
5. Ingresá cantidad.
6. Configurá Stop/Take si querés.
7. Confirmá.

### Cerrar

Seleccioná una posición y presioná **Cerrar posición**.

### Actualización de precios

Mientras Paper Trading permanece abierto, los precios se consultan aproximadamente cada 60 segundos.

También existe **Actualizar ahora**.

## 15. Stop Loss y Take Profit automáticos

Para una posición long:

- si el precio observado es `<= Stop`, se cierra por Stop Loss;
- si el precio observado es `>= Take`, se cierra por Take Profit.

El cierre es simulado.

## 16. AUTO por activo

Permite administrar automáticamente un activo analizado.

Parámetros:

- activar / pausar;
- estrategia;
- capital máximo;
- riesgo %;
- Stop %;
- Take %.

Las señales usan la última vela cerrada.

La cantidad se limita por capital y riesgo.

## 17. Cartera AUTO multi-activo

Abrí:

```text
Paper Trading → Cartera AUTO
```

Parámetros:

- Score mínimo;
- Máx. total;
- Máx. Argentina;
- Máx. Internacional;
- Máx. Crypto;
- Riesgo global %;
- Riesgo por operación %;
- Capital máximo por operación %;
- Stop Loss %;
- Take Profit %.

### Requisitos para abrir una posición

Deben cumplirse todos:

1. candidato válido;
2. señal `ENTRADA`;
3. Score mínimo;
4. no existir una posición duplicada;
5. no superar el máximo total;
6. no superar el máximo de su mercado;
7. tener efectivo;
8. disponer de presupuesto de riesgo.

### Riesgo global

El riesgo estimado de una posición long es aproximadamente:

```text
cantidad × (precio de entrada - Stop Loss)
```

IA-TradeX suma ese riesgo por cuenta ARS/USD.

### Exposición

La exposición es el valor de mercado de las posiciones abiertas.

Se muestra:

- exposición ARS;
- exposición USD;
- exposición % sobre equity;
- riesgo ARS;
- riesgo USD;
- riesgo % sobre equity.

### Ranking continuo

Cada ciclo guarda el ranking más reciente.

Decisiones posibles:

```text
ABIERTA
SIN ENTRADA
SCORE BAJO
LÍMITE TOTAL
LÍMITE Argentina
LÍMITE Internacional
LÍMITE Cripto
RIESGO GLOBAL
DUPLICADA
SIN EFECTIVO
CANTIDAD 0
ERROR
```

## 18. Performance

Abrí **Performance** desde la pantalla principal.

Elegí:

```text
ARS
USD
```

Las monedas nunca se mezclan en las métricas.

### Métricas

- Equity
- Retorno
- P&L realizado
- P&L no realizado
- Win Rate
- Profit Factor
- Drawdown realizado
- Operaciones cerradas

### Por estrategia

Agrupa el historial según la estrategia registrada.

### Por mercado

Agrupa por Argentina, Internacional, Crypto u otros contextos almacenados.

### Por régimen

Agrupa según el régimen guardado al abrir la operación.

### Métricas por grupo

Cada fila muestra:

- operaciones;
- Win Rate;
- P&L;
- P&L medio %;
- Profit Factor;
- drawdown realizado;
- mejor operación;
- peor operación.

## 19. Performance vs Buy & Hold

Si hay un análisis actual en la misma moneda, Performance muestra:

- retorno histórico de la estrategia;
- retorno Buy & Hold;
- diferencia en puntos porcentuales.

Esta comparación pertenece al backtest actual, no al historial de Paper Trading.

## 20. Exportar CSV

### Operaciones

Botón:

```text
Exportar operaciones CSV
```

Incluye:

- símbolo;
- mercado;
- moneda;
- cantidad;
- entrada;
- salida;
- P&L;
- P&L %;
- estrategia;
- régimen;
- apertura;
- cierre;
- motivo.

### Estadísticas

Botón:

```text
Exportar estadísticas CSV
```

Exporta:

- resumen de la cuenta seleccionada;
- estadísticas por estrategia;
- estadísticas por mercado;
- estadísticas por régimen.

## 21. Persistencia segura

Directorio:

```text
~/.ia-tradex/
```

Archivos:

```text
paper-trading.json
paper-trading.json.bak
watchlists.json
watchlists.json.bak
```

La aplicación escribe primero en un temporal y luego reemplaza el archivo principal. Cuando existe un estado anterior también conserva `.bak`.

## 22. Ejecutar desde código

```bash
mvn clean javafx:run
```

Validar compilación:

```bash
./verify-project.sh
```

## 23. Crear la aplicación macOS

```bash
./package-macos.sh
```

Resultado:

```text
dist/IA-TradeX.app
```

Crear DMG:

```bash
./package-macos-dmg.sh
```

## 24. Icono y menú de macOS

El icono de aplicación/Dock utiliza solamente el isotipo IX.

**Acerca de IA-TradeX** está en el menú nativo de macOS.

## 25. Limitaciones actuales

- no envía órdenes reales;
- no conecta brokers;
- el modelo ML de v2.0 trabaja sobre un solo activo analizado por vez;
- el ML todavía no filtra automáticamente Scanner ni Cartera AUTO;
- la robustez v1.4 es principalmente temporal sobre el activo actual;
- todavía no existe validación cruzada masiva entre muchos activos;
- algunas fuentes públicas pueden estar demoradas;
- la automatización depende de consultas periódicas, no de ejecución tick-by-tick.

## 26. Próxima etapa

Después de v2.0 conviene concentrarse en validar el modelo antes de darle control operativo:

1. validación cruzada del ML en múltiples activos;
2. calibración de probabilidades;
3. comparación contra otros modelos simples;
4. Monte Carlo / sensibilidad;
5. recién después usar ML como filtro opcional de Paper Trading.

## 27. Acerca de

**IA-TradeX v2.0.0**

**Autor:** Juan Manuel De Castro  
**Email:** jm@pronexo.com  
**Web:** https://www.pronexo.com

Licencia:

```text
GNU AGPL v3.0
```


## 28. Validación de estrategias

Primero ejecutá un análisis normal y luego presioná **Validación**.

El laboratorio usa el activo, timeframe y período actualmente cargados.

Se recomiendan períodos suficientemente largos. IA-TradeX exige al menos 80 velas.

### Walk-Forward

Walk-Forward intenta responder:

> ¿La estrategia sigue funcionando cuando los parámetros se eligen usando solamente el pasado y se prueban en un período posterior?

Para cada fold, IA-TradeX selecciona Riesgo, Stop y Take con el tramo de entrenamiento y luego congela esos valores durante la validación siguiente.

### Out-of-Sample

El histórico se separa aproximadamente:

```text
70% In-Sample
30% Out-of-Sample
```

El OOS funciona como examen final y no participa en la búsqueda de parámetros.

### Robustez

La tabla muestra:

- In-Sample;
- OOS;
- Buy & Hold OOS;
- retorno medio Walk-Forward;
- folds Walk-Forward positivos;
- Score de Robustez;
- clasificación;
- explicación.

Clasificaciones:

**ROBUSTA**  
El OOS fue positivo y la mayoría de los folds Walk-Forward sostuvo un comportamiento compatible.

**DUDOSA**  
La evidencia es mixta o insuficiente.

**SOBREAJUSTADA**  
El buen resultado del entrenamiento no se sostuvo fuera de muestra.

Estas etiquetas son reglas de diagnóstico, no garantías.

### Optimización controlada

La pestaña **Optimización controlada** muestra para cada estrategia:

- Riesgo seleccionado;
- Stop seleccionado;
- Take seleccionado;
- retorno en entrenamiento;
- retorno OOS;
- Buy & Hold OOS;
- cantidad de operaciones Train/OOS;
- clasificación.

La grilla es deliberadamente pequeña para reducir el riesgo de encontrar una combinación espectacular por casualidad.

### Exportar validación

Presioná:

```text
Exportar validación CSV
```

El archivo puede utilizarse para comparar resultados externamente.

## 29. Qué cambió entre v1.0 y v1.4

### v1.1
Walk-Forward con parámetros seleccionados únicamente en cada tramo de entrenamiento.

### v1.2
Out-of-Sample reservado cronológicamente.

### v1.3
Score y clasificación de robustez temporal.

### v1.4
Optimización controlada de Riesgo / Stop / Take y evaluación posterior sobre OOS.

## 30. Qué significa estar listo para ML

La salida `ROBUSTA` no significa que una estrategia deba utilizarse con dinero real.

La v2.0 ya incorpora un primer modelo ML, pero antes de usarlo como filtro de ejecución sigue siendo conveniente sumar:

- validación cruzada en muchos activos;
- más períodos;
- Monte Carlo;
- sensibilidad a costos;
- pruebas con datos nuevos acumulados en el tiempo.



## 31. IA / Machine Learning

Primero analizá un activo y luego presioná **IA / ML**.

La ventana entrena un modelo nuevo para ese activo y período.

### Qué intenta aprender

El modelo clasifica si, dentro de 5 velas, el cierre supera al cierre actual en más de 0,5%.

Ejemplo:

```text
cierre actual = 100
umbral positivo = 100,50
```

Si el cierre dentro de 5 velas supera ese nivel, el ejemplo histórico se marca como clase positiva.

### Qué variables mira

- retorno 1 vela;
- retorno 5 velas;
- retorno 20 velas;
- EMA 12 vs EMA 26;
- precio vs EMA 26;
- RSI;
- ATR relativo;
- rango de vela;
- volumen relativo.

### Entrenamiento y OOS

El histórico se separa cronológicamente.

El modelo no puede usar ejemplos cuya etiqueta futura atraviese la frontera entre Train y OOS.

La media y desviación usadas para normalizar las variables se calculan solamente con Train.

### Balanced Accuracy

Promedia la capacidad de reconocer ejemplos positivos y negativos.

Es especialmente útil cuando una de las clases aparece con más frecuencia que la otra.

### Precision

De todos los casos que el modelo marcó positivos, qué proporción realmente fue positiva históricamente en OOS.

### Recall

De todos los casos positivos del OOS, qué proporción encontró el modelo.

### Brier Score

Mide el error de las probabilidades.

Menor es mejor.

IA-TradeX muestra también **Baseline Brier**. Si el modelo no mejora al baseline, la señal se considera débil aunque la probabilidad actual sea alta.

### Decisiones

**FAVORABLE**  
El modelo supera el umbral interno y además muestra una calidad OOS superior al baseline.

**OBSERVAR**  
No existe evidencia suficiente para una señal fuerte.

**NO OPERAR**  
La probabilidad de la clase favorable es baja.

### Importancia de variables

La tabla muestra:

- variable;
- peso;
- importancia relativa;
- dirección de influencia.

Un peso positivo empuja la clasificación hacia favorable. Un peso negativo la empuja hacia desfavorable.

### Limitación importante

La probabilidad del modelo es:

> probabilidad estimada de la etiqueta que aprendió el modelo

No debe interpretarse como:

> probabilidad real de ganar dinero

### Exportar

Presioná:

```text
Exportar modelo CSV
```

para guardar métricas y pesos del reporte actual.

## 32. Relación entre Validación y ML

La v1.4 valida estrategias basadas en reglas.

La v2.0 agrega un modelo estadístico independiente.

Por ahora:

```text
Backtest / Validación → estudia estrategias
ML                  → estudia contexto favorable
Paper Trading       → simula operaciones
```

El ML todavía no activa ni bloquea automáticamente operaciones de Paper Trading.

## 33. Recomendación para pruebas

Para obtener suficientes ejemplos, usá períodos largos.

En activos diarios, `1y` será generalmente más útil que `3m`.

Si la ventana indica que faltan muestras Train/OOS, elegí más histórico.

