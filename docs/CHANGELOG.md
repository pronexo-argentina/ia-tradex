# Changelog


## 0.7.0

- Primera versión de Paper Trading manual.
- Cuentas simuladas ARS y USD.
- Capital inicial configurable.
- Persistencia local en `~/.ia-tradex/paper-trading.json`.
- Compra manual del activo analizado.
- Cierre manual de posiciones.
- P&L no realizado y equity.
- Stop Loss y Take Profit registrados.
- Contexto de estrategia y régimen guardado por operación.
- Historial persistente de operaciones cerradas.
- Nuevo botón Paper Trading.
- Acerca de reemplazado por diálogo oscuro integrado visualmente con IA-TradeX.


## 0.6.3

- Licencia migrada a GNU AGPLv3.
- README renovado para GitHub.
- Capturas del producto agregadas a docs/images.
- Sección Acerca de con autor, email y web.
- Botón Acerca de agregado a la aplicación.
- .gitignore ampliado para IDE, build, macOS, logs, paquetes y secretos.


## 0.6.2

- Corregida barra superior responsive.
- Eliminado el uso del mismo control JavaFX en dos contenedores simultáneos.
- La barra superior ahora usa un único FlowPane que reacomoda los controles automáticamente.
- Se mantiene el scroll vertical global.



## 0.6.1

- Scroll vertical global del dashboard.
- Layout responsive para ventanas angostas.
- Barra superior con modo ancho y compacto.
- Selectores superiores reacomodables.
- Métricas y régimen adaptables.
- Menor ancho mínimo de ventana.
- Se evita scroll horizontal del dashboard.



## 0.6.0

- Motor de detección de régimen 100% Java.
- Clasificación ALCISTA / BAJISTA / LATERAL.
- Fuerza BAJA / MEDIA / ALTA.
- Volatilidad relativa basada en ATR porcentual vs mediana histórica del activo.
- Panel visual de régimen.
- Estrategias compatibles según contexto.
- Explicación textual de los criterios utilizados.
- La clasificación continúa siendo por reglas, no Machine Learning.


## 0.5.2

- Corregido disparo del autocomplete en Argentina.
- El debounce de 300 ms ahora se ejecuta en Argentina e Internacional.
- El listener de texto ya no filtra únicamente `stocks`.
- Selección rápida del texto habilitada también para Argentina.


## 0.5.1

- Corregido autocomplete del mercado Argentina.
- El popup ya no descarta resultados porque el market type sea `argentina`.
- La búsqueda captura el mercado antes de iniciar el thread para no leer controles JavaFX desde un hilo secundario.
- Se descartan respuestas viejas si el usuario sigue escribiendo.



## 0.5.0

- Argentina migra de la API autenticada de BYMA a Open BYMADATA.
- Eliminadas credenciales OAuth y archivos de configuración.
- No requiere usuario, contraseña, client_id ni client_secret.
- CEDEARs, panel líder y panel general mediante endpoints públicos.
- Cotización local con último precio, variación, bid, ask y cantidades.
- Histórico OHLCV diario mediante la serie pública de Open BYMADATA.
- Plazo inicial de cotización: 24HS.
- Datos aproximadamente 20 minutos demorados.



## 0.4.0

- Argentina deja de usar Yahoo Finance.
- Integración oficial preparada con BYMA Market Data.
- OAuth 2.0 Client Credentials.
- Búsqueda BYMA de Acciones y CEDEARs.
- Histórico diario por EOD Equity.
- Caché local de respuestas EOD por fecha y categoría.
- Último precio, variación, compra, venta y cantidades en el panel técnico.
- Timeframe Argentina limitado a 1d mientras la fuente oficial usada sea EOD.
- Archivo `byma.properties.example` sin secretos.



## 0.3.0

- Selector de mercado separado en Criptomonedas, Argentina e Internacional.
- Búsqueda argentina prioriza instrumentos Yahoo de Buenos Aires (`.BA`).
- Cotizaciones argentinas mostradas explícitamente en ARS.
- Cotizaciones internacionales separadas de sus equivalentes locales.
- Evita confundir CEDEARs con acciones extranjeras subyacentes.



## 0.2.0

- Motor de múltiples estrategias 100% Java.
- EMA Cross.
- Momentum.
- Mean Reversion.
- Breakout.
- Comparador histórico de retorno, Profit Factor, Drawdown, Sharpe y operaciones.
- Selección interactiva de estrategia para actualizar métricas, equity y trades.
- Etiqueta explícita "MEJOR HISTÓRICO" para evitar confundir backtest con predicción.
- Las cuatro estrategias usan señal de vela previa y ejecución en apertura siguiente.


## 0.1.0

- Corregido launcher para aplicaciones JavaFX empaquetadas con jpackage.

- Icono oficial IA-TradeX integrado en JavaFX.
- Packaging macOS preparado con jpackage.
- Script para generar IA-TradeX.app de doble clic.
- Script opcional para generar instalador DMG.

- Primera versión de IA-TradeX.
- Arquitectura 100% Java.
- JavaFX 23.
- Java 21.
- Eliminada la dependencia de Python/FastAPI.
- Binance REST.
- Kraken REST.
- Yahoo Finance REST.
- Buscador/autocomplete de acciones y ETF.
- Logos con fallback de iniciales.
- EMA 12/26.
- RSI 14.
- ATR 14.
- Backtesting con comisión, slippage, stop-loss y take-profit.
- Profit Factor, Sharpe, Drawdown, Win Rate y Buy & Hold.
- Gráficos de precio y equity.
- Tabla de operaciones.
