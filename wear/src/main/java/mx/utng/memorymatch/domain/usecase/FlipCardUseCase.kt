package mx.utng.memorymatch.domain.usecase

import mx.utng.memorymatch.domain.model.GamePhase
import mx.utng.memorymatch.domain.model.GameState

/**
 * Caso de uso puro.
 * Retorna el nuevo GameState con la tarjeta volteada,
 * o el mismo estado si el toque no es válido.
 */
class FlipCardUseCase {
    operator fun invoke(state: GameState, cardIndex: Int): GameState {
        val card = state.board[cardIndex]

        // Ignorar si ya está revelada o ya fue encontrada
        if (card.isFlipped || card.isMatched) return state

        // Ignorar si ya hay 2 tarjetas seleccionadas (en fase CHECKING)
        if (state.secondSelected != null) return state

        val newBoard = state.board.mapIndexed { i, c ->
            if (i == cardIndex) c.copy(isFlipped = true) else c
        }

        return when {
            // Primera selección
            state.firstSelected == null -> state.copy(
                board         = newBoard,
                firstSelected = cardIndex,
                phase         = GamePhase.WAITING_SECOND
            )
            // Segunda selección
            else -> state.copy(
                board          = newBoard,
                secondSelected = cardIndex,
                phase          = GamePhase.CHECKING,
                moves          = state.moves + 1
            )
        }
    }
}
