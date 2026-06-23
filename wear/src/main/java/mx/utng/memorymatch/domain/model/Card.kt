package mx.utng.memorymatch.domain.model

/**
 * Entidad inmutable que representa una tarjeta del tablero.
 * Se actualiza siempre con .copy() — nunca mutando directamente.
 */
data class Card(
    val id: Int,                          // 0-11 (12 tarjetas = 6 pares)
    val symbol: CardSymbol,               // qué par es esta tarjeta
    val isFlipped: Boolean  = false,      // ¿está boca arriba?
    val isMatched: Boolean  = false,      // ¿ya se encontró el par?
)
