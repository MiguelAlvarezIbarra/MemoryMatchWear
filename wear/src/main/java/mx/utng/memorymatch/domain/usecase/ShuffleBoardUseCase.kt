package mx.utng.memorymatch.domain.usecase

import mx.utng.memorymatch.domain.model.Card
import mx.utng.memorymatch.domain.model.CardSymbol

/**
 * Caso de uso puro (sin dependencias Android).
 * Crea 12 tarjetas (2 de cada símbolo) mezcladas aleatoriamente.
 */
class ShuffleBoardUseCase {
    /** Retorna la lista de 12 tarjetas barajadas con IDs 0..11. */
    operator fun invoke(): List<Card> =
        CardSymbol.values()
            .flatMap { symbol -> listOf(symbol, symbol) } // duplicar cada símbolo
            .shuffled()                                    // mezclar
            .mapIndexed { index, symbol ->                 // asignar id secuencial
                Card(id = index, symbol = symbol)
            }
}
