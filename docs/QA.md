# QA de IA-TradeX v2.0.0

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
