# QA de IA-TradeX v1.0.0

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
