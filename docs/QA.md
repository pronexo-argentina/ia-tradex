# QA de IA-TradeX v2.2.0

Checklist recomendado antes de publicar una release.

## Build

```bash
./verify-project.sh
```

Debe finalizar con compilación correcta.

## Dashboard

- abrir Crypto, Argentina e Internacional;
- verificar Mercado, Fuente, Activo, Vela y Período;
- ejecutar análisis;
- revisar métricas, régimen, estrategias, gráficos y tabla de operaciones.

## Scanner

- abrir Scanner;
- agregar y quitar un activo;
- cerrar y volver a abrir para validar persistencia;
- ejecutar Scanner manual;
- probar Auto 60 s;
- abrir un resultado en el dashboard.

## Paper Trading

- alternar ARS/USD;
- modificar capital inicial sin posiciones abiertas;
- abrir una posición manual;
- cerrar una posición;
- probar Stop y Take;
- revisar historial.

## AUTO por activo

- cargar un activo analizado;
- configurar estrategia/riesgo;
- activar;
- verificar Actividad AUTO;
- pausar.

## Cartera AUTO

- configurar Score mínimo;
- límites total y por mercado;
- riesgo global;
- ejecutar ahora;
- revisar exposición y ranking;
- verificar que no abra duplicados.

## Performance

- alternar ARS/USD;
- revisar métricas generales;
- revisar pestañas por estrategia/mercado/régimen;
- exportar operaciones CSV;
- exportar estadísticas CSV.

## Persistencia

Después de una modificación de estado deberían existir los archivos principales y, tras una segunda escritura, sus backups `.bak`.

## macOS

```bash
./package-macos.sh
```

Comprobar:

- apertura por doble clic;
- isotipo en Dock;
- menú nativo `IA-TradeX → Acerca de IA-TradeX`;
- cierre y reapertura de la aplicación.


## Validación v1.4

- analizar un activo con 80+ velas;
- abrir `Validación`;
- verificar ejecución automática en background;
- revisar cuatro estrategias;
- comprobar In-Sample / OOS / Buy & Hold OOS;
- revisar folds Walk-Forward;
- revisar clasificación de robustez;
- abrir pestaña Optimización controlada;
- verificar parámetros y resultados Train/OOS;
- exportar CSV;
- repetir con un período de menos de 80 velas y confirmar mensaje de historial insuficiente.


## Machine Learning v2.0

- analizar un activo con histórico largo;
- abrir `IA / ML`;
- verificar ejecución en background;
- confirmar muestras Train / OOS;
- revisar probabilidad y decisión;
- revisar Balanced Accuracy, Precision y Recall;
- comprobar Brier y Baseline Brier;
- revisar tabla de importancia de variables;
- exportar CSV;
- repetir con un período corto y confirmar mensaje de historial insuficiente;
- verificar que entrenar ML no abra posiciones de Paper Trading;
- verificar que Cartera AUTO y Scanner sigan funcionando sin depender del modelo.


## Scanner + ML v2.1

- ejecutar Scanner con ML Desactivado;
- confirmar columnas ML en estado OFF;
- ejecutar Scanner con ML Informativo;
- verificar ML / ML % sin alterar una ENTRADA técnica;
- ejecutar Scanner con ML Confirmación;
- verificar `BLOQUEADA ML` cuando no sea FAVORABLE;
- probar un período corto sin muestras suficientes;
- confirmar `NO DISPONIBLE` y bloqueo conservador en Confirmación;
- verificar que un error ML no convierta en error el análisis técnico del activo.

## Cartera AUTO + ML v2.1

- guardar cada uno de los tres modos ML;
- cerrar/reabrir la aplicación y comprobar persistencia;
- ejecutar con Desactivado y comparar comportamiento anterior;
- ejecutar con Informativo y revisar ranking/log;
- ejecutar con Confirmación;
- verificar que ML no favorable impida BUY;
- verificar que ML favorable siga pasando por Score, límites, efectivo y riesgo;
- comprobar que Stop Loss, Take Profit y salidas por estrategia siguen funcionando;
- revisar compatibilidad de `paper-trading.json` creado por versiones anteriores.


## Memoria ML v2.2

- entrenar IA / ML sobre un activo;
- abrir Memoria ML y verificar una sola decisión PENDING;
- volver a entrenar sobre la misma vela y confirmar que no duplica;
- comprobar `~/.ia-tradex/ml-decisions.json`;
- provocar una segunda escritura y comprobar `.bak`;
- cuando existan cinco velas cerradas posteriores, volver a analizar el mismo activo;
- comprobar cambio PENDING → RESOLVED;
- validar retorno a cinco velas;
- validar etiqueta POSITIVA / NO POSITIVA;
- confirmar acierto para FAVORABLE y NO OPERAR;
- confirmar que OBSERVAR queda con acierto `—`;
- verificar métricas agregadas;
- exportar memoria CSV;
- comprobar que una vela todavía abierta nunca resuelve una decisión;
- probar resolución con período móvil donde la vela original ya no esté incluida.
