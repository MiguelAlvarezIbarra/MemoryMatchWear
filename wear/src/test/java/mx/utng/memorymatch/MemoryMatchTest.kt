package mx.utng.memorymatch

import mx.utng.memorymatch.domain.model.CardSymbol
import mx.utng.memorymatch.domain.model.GamePhase
import mx.utng.memorymatch.domain.model.GameState
import mx.utng.memorymatch.domain.usecase.CheckMatchUseCase
import mx.utng.memorymatch.domain.usecase.FlipCardUseCase
import mx.utng.memorymatch.domain.usecase.MatchResult
import mx.utng.memorymatch.domain.usecase.ShuffleBoardUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios sin emulador — prueban la lógica pura del dominio.
 * Ejecutar con: ./gradlew :wear:test
 */
class MemoryMatchTest {

    private lateinit var shuffle: ShuffleBoardUseCase
    private lateinit var flip: FlipCardUseCase
    private lateinit var check: CheckMatchUseCase

    @Before
    fun setup() {
        shuffle = ShuffleBoardUseCase()
        flip    = FlipCardUseCase()
        check   = CheckMatchUseCase()
    }

    // ── ShuffleBoardUseCase ──────────────────────────────────────────────────

    @Test
    fun `board has 12 cards`() {
        assertEquals(12, shuffle().size)
    }

    @Test
    fun `board has exactly 2 of each symbol`() {
        val board = shuffle()
        CardSymbol.values().forEach { symbol ->
            assertEquals(
                "Símbolo $symbol debería aparecer exactamente 2 veces",
                2,
                board.count { it.symbol == symbol }
            )
        }
    }

    @Test
    fun `board cards have sequential ids from 0 to 11`() {
        val board = shuffle()
        val ids = board.map { it.id }.sorted()
        assertEquals((0..11).toList(), ids)
    }

    @Test
    fun `all cards start face down and unmatched`() {
        val board = shuffle()
        assertTrue("Todas las tarjetas deben iniciar boca abajo",
            board.all { !it.isFlipped && !it.isMatched })
    }

    // ── FlipCardUseCase ──────────────────────────────────────────────────────

    @Test
    fun `flip sets card isFlipped to true`() {
        val board = shuffle()
        val state = GameState(board = board, phase = GamePhase.SELECTING_FIRST)
        val next  = flip(state, 0)

        assertTrue("La tarjeta 0 debe quedar boca arriba", next.board[0].isFlipped)
        assertEquals("firstSelected debe ser 0", 0, next.firstSelected)
        assertEquals("La fase debe cambiar a WAITING_SECOND",
            GamePhase.WAITING_SECOND, next.phase)
    }

    @Test
    fun `flip second card increments moves and changes phase to CHECKING`() {
        val board = shuffle()
        val stateAfterFirst = flip(
            GameState(board = board, phase = GamePhase.SELECTING_FIRST), 0
        )
        val stateAfterSecond = flip(stateAfterFirst, 1)

        assertEquals("Debe haber 1 movimiento", 1, stateAfterSecond.moves)
        assertEquals("La fase debe ser CHECKING", GamePhase.CHECKING, stateAfterSecond.phase)
        assertNotNull("secondSelected no debe ser null", stateAfterSecond.secondSelected)
    }

    @Test
    fun `flip ignores already matched card`() {
        val board = shuffle().toMutableList()
        board[0] = board[0].copy(isMatched = true)
        val state = GameState(board = board, phase = GamePhase.SELECTING_FIRST)
        val next  = flip(state, 0)

        // El estado NO debe cambiar
        assertNull("firstSelected debe seguir siendo null", next.firstSelected)
        assertEquals("La fase no debe cambiar", GamePhase.SELECTING_FIRST, next.phase)
    }

    @Test
    fun `flip ignores already flipped card`() {
        val board = shuffle().toMutableList()
        board[0] = board[0].copy(isFlipped = true)
        val state = GameState(board = board, phase = GamePhase.SELECTING_FIRST)
        val next  = flip(state, 0)

        assertNull("firstSelected debe seguir siendo null", next.firstSelected)
    }

    // ── CheckMatchUseCase ────────────────────────────────────────────────────

    @Test
    fun `checkMatch returns HIT when symbols match`() {
        val board = shuffle().toMutableList()
        // Forzar mismo símbolo en posiciones 0 y 1
        board[0] = board[0].copy(symbol = CardSymbol.COMPOSE, isFlipped = true)
        board[1] = board[1].copy(symbol = CardSymbol.COMPOSE, isFlipped = true)
        val state = GameState(board = board, firstSelected = 0, secondSelected = 1)

        assertEquals(MatchResult.HIT, check(state))
    }

    @Test
    fun `checkMatch returns MISS when symbols differ`() {
        val board = shuffle().toMutableList()
        board[0] = board[0].copy(symbol = CardSymbol.COMPOSE, isFlipped = true)
        board[1] = board[1].copy(symbol = CardSymbol.ROOM,    isFlipped = true)
        val state = GameState(board = board, firstSelected = 0, secondSelected = 1)

        assertEquals(MatchResult.MISS, check(state))
    }

    @Test
    fun `checkMatch returns PENDING when no cards selected`() {
        val board = shuffle()
        val state = GameState(board = board)   // firstSelected = null

        assertEquals(MatchResult.PENDING, check(state))
    }

    @Test
    fun `checkMatch returns PENDING when only one card selected`() {
        val board = shuffle()
        val state = GameState(board = board, firstSelected = 0)  // secondSelected = null

        assertEquals(MatchResult.PENDING, check(state))
    }

    // ── Ejercicio extra: isComplete ──────────────────────────────────────────

    @Test
    fun `GameState isComplete is true when all pairs are found`() {
        val state = GameState(matchesFound = GameState.TOTAL_PAIRS)
        assertTrue("isComplete debe ser true con todos los pares", state.isComplete)
    }

    @Test
    fun `GameState isComplete is false when some pairs are missing`() {
        val state = GameState(matchesFound = 3)
        assertFalse("isComplete debe ser false con pares pendientes", state.isComplete)
    }
}
