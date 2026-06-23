package mx.utng.memorymatch.domain.model

/**
 * Estado inmutable completo del juego.
 * La UI reacciona a cambios en este objeto vía StateFlow.
 */
data class GameState(
    val board: List<Card>        = emptyList(), // las 12 tarjetas
    val phase: GamePhase         = GamePhase.IDLE,
    val firstSelected: Int?      = null,        // índice de la 1a tarjeta
    val secondSelected: Int?     = null,        // índice de la 2a tarjeta
    val matchesFound: Int        = 0,           // pares encontrados
    val moves: Int               = 0,           // intentos totales
    val elapsedSeconds: Long     = 0L,          // tiempo transcurrido
    val bestTime: Long           = Long.MAX_VALUE,
) {
    /** El juego está completo cuando se encontraron todos los pares. */
    val isComplete: Boolean get() = matchesFound == TOTAL_PAIRS

    companion object {
        const val TOTAL_PAIRS = 6
        const val TOTAL_CARDS = TOTAL_PAIRS * 2  // 12
    }
}

/**
 * Fases del ciclo de vida del juego (máquina de estados).
 */
enum class GamePhase {
    IDLE,             // tablero visible, esperando primer toque
    SELECTING_FIRST,  // esperando que el jugador elija una tarjeta
    WAITING_SECOND,   // primera tarjeta revelada, espera la 2a
    CHECKING,         // evaluando si hacen par (800ms de pausa)
    WON               // todos los pares encontrados
}
