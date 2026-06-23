package mx.utng.memorymatch.domain.usecase

import mx.utng.memorymatch.domain.model.GameState

/**
 * Caso de uso puro.
 * Evalúa si las dos tarjetas seleccionadas forman un par.
 * Compara SÍMBOLOS, nunca IDs.
 */
class CheckMatchUseCase {
    operator fun invoke(state: GameState): MatchResult {
        val first  = state.firstSelected  ?: return MatchResult.PENDING
        val second = state.secondSelected ?: return MatchResult.PENDING

        val cardA = state.board[first]
        val cardB = state.board[second]

        return if (cardA.symbol == cardB.symbol) MatchResult.HIT
               else MatchResult.MISS
    }
}

enum class MatchResult { HIT, MISS, PENDING }
