# 🧠 Memory Match — Wear OS

**Juego de memoria para smartwatch** · Componentes Android como pares · Compose for Wear OS · UTNG 2025

---

## Descripción

Memory Match muestra una cuadrícula de **12 tarjetas boca abajo** (6 pares) en la pantalla circular del reloj. El jugador las voltea de dos en dos buscando pares de conceptos Android (StateFlow, ViewModel, Room, Flow, Compose, DataLayer). Al completar todos los pares gana con animación y vibración háptica.

### Mecánica del juego

| Estado | Acción del jugador | Sistema |
|--------|-------------------|---------|
| SELECTING_FIRST | Toca una tarjeta | Flip animado → WAITING_SECOND |
| WAITING_SECOND | Toca una segunda | Flip → evalúa par |
| CHECKING | (automático 800ms) | HIT → MATCHED / MISS → voltean |
| WON | Ve pantalla de victoria | Timer parado, récord guardado |

### Los 6 pares de tarjetas

| # | Emoji | Etiqueta | Concepto Android |
|---|-------|----------|-----------------|
| 1 | ⚡ | StateFlow | Flujo de estado reactivo |
| 2 | 🏛 | ViewModel | Lógica de presentación |
| 3 | 🗄 | Room | Base de datos SQLite |
| 4 | 🔄 | Flow | Stream asíncrono |
| 5 | 🎨 | Compose | Framework declarativo |
| 6 | 🔗 | DataLayer | Wearable Data Layer API |

---

## Stack

| Componente | Tecnología |
|-----------|-----------|
| UI | Compose for Wear OS (`LazyVerticalGrid` circular) |
| Arquitectura | MVVM + Clean Architecture |
| Animación | `animateFloatAsState` + `graphicsLayer` (flip 3D) |
| Estado | `GameState` inmutable + `StateFlow<GameState>` |
| Persistencia | DataStore Preferences (mejor tiempo) |
| Haptics | `LocalHapticFeedback` (match, error, victoria) |
| Coroutines | `viewModelScope.launch` para el delay de 800ms |
| Testing | JUnit 4 — lógica pura sin emulador |

---

## Arquitectura

```
domain/
├── model/        ← Card, CardSymbol, GameState (sin imports Android)
├── usecase/      ← ShuffleBoard, FlipCard, CheckMatch, BestTime
└── repository/   ← BestTimeRepository (interfaz)

data/
├── datasource/   ← BestTimeDataSource (DataStore)
└── repository/   ← BestTimeRepositoryImpl

presentation/
├── GameActivity.kt
├── MemoryViewModelFactory.kt
└── board/
    ├── MemoryViewModel.kt
    ├── BoardScreen.kt
    ├── CardItem.kt
    └── VictoryScreen.kt
```

**Regla de Dependencia:** `Presentación → Dominio ← Datos`

---

## Commits

```
chore: setup MemoryMatchWear project with Compose for Wear OS and animation dependencies
feat: add domain models — CardSymbol, Card and immutable GameState
feat: add domain use cases — ShuffleBoardUseCase, FlipCardUseCase, CheckMatchUseCase
feat: add data layer — BestTimeDataSource and BestTimeRepositoryImpl
feat: add MemoryViewModel with flip logic, match evaluation, timer and haptic effects
feat: add CardItem composable with 3D flip animation using graphicsLayer and animateFloatAsState
feat: add BoardScreen with 3x4 grid, haptic effects and progress indicator
feat: add VictoryScreen with best time display
test: add MemoryMatch unit tests for board creation, flip and match logic
feat: add DI factory and GameActivity — v1.0.0
```

---

## Tests unitarios

```bash
./gradlew :wear:test
```

| Test | Verifica |
|------|---------|
| `board has 12 cards` | ShuffleBoardUseCase produce 12 tarjetas |
| `board has exactly 2 of each symbol` | 2 tarjetas por símbolo |
| `board cards have sequential ids` | IDs 0..11 |
| `all cards start face down` | isFlipped=false, isMatched=false |
| `flip sets card isFlipped to true` | FlipCardUseCase voltea correctamente |
| `flip second card increments moves` | Contador de movimientos |
| `flip ignores already matched card` | Tarjetas encontradas no se tocan |
| `flip ignores already flipped card` | Tarjetas reveladas no se duplican |
| `checkMatch returns HIT` | Mismos símbolos → HIT |
| `checkMatch returns MISS` | Símbolos distintos → MISS |
| `checkMatch returns PENDING` | Sin selección → PENDING |
| `GameState isComplete when all pairs found` | Propiedad `isComplete` |

---

## Buenas prácticas aplicadas

- **Inmutabilidad** — `GameState` con `.copy()`, nunca mutación directa
- **SRP** — flip / check / shuffle en clases separadas
- **UDF** — `StateFlow` como única fuente de verdad
- **Channel para efectos** — efectos hápticos separados del estado
- **Funciones puras** — casos de uso sin efectos secundarios
- **Tests sin emulador** — dominio puro, JUnit 4
- **Separación** — animación en Composable, lógica en ViewModel

---

## Alumno

| Campo | Valor |
|-------|-------|
| Nombre | Ross Verne |
| Materia | DDI (Desarrollo de Dispositivos Inteligentes) |
| Universidad | UTNG |
| Año | 2025 |
