package mx.utng.memorymatch.presentation.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mx.utng.memorymatch.domain.model.GamePhase
import mx.utng.memorymatch.domain.model.GameState
import mx.utng.memorymatch.domain.usecase.CheckMatchUseCase
import mx.utng.memorymatch.domain.usecase.FlipCardUseCase
import mx.utng.memorymatch.domain.usecase.GetBestTimeUseCase
import mx.utng.memorymatch.domain.usecase.MatchResult
import mx.utng.memorymatch.domain.usecase.SaveBestTimeUseCase
import mx.utng.memorymatch.domain.usecase.ShuffleBoardUseCase

/**
 * ViewModel que orquesta todos los casos de uso.
 * Expone estado vía StateFlow (UDF) y efectos hápticos vía Channel.
 */
class MemoryViewModel(
    private val shuffleBoard : ShuffleBoardUseCase,
    private val flipCard     : FlipCardUseCase,
    private val checkMatch   : CheckMatchUseCase,
    private val saveBestTime : SaveBestTimeUseCase,
    private val getBestTime  : GetBestTimeUseCase,
) : ViewModel() {

    // ── Estado principal (UDF) ──────────────────────────────────────────────
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // ── Canal de efectos de una sola vez (haptics) ──────────────────────────
    // Separado del StateFlow para no mezclar estado persistente con eventos.
    private val _effects = Channel<GameEffect>(Channel.BUFFERED)
    val effects: Flow<GameEffect> = _effects.receiveAsFlow()

    private var timerJob: Job? = null

    init { startNewGame() }

    // ── Acciones públicas ────────────────────────────────────────────────────

    fun startNewGame() {
        timerJob?.cancel()
        val board    = shuffleBoard()
        val bestTime = runBlocking { getBestTime() }
        _state.value = GameState(
            board    = board,
            phase    = GamePhase.SELECTING_FIRST,
            bestTime = bestTime
        )
        startTimer()
    }

    fun onCardTapped(cardIndex: Int) {
        val current = _state.value
        // Bloquear toques durante la evaluación del par o cuando ya ganó
        if (current.phase == GamePhase.CHECKING || current.phase == GamePhase.WON) return

        val afterFlip = flipCard(current, cardIndex)
        _state.value = afterFlip

        // Si ya hay dos tarjetas seleccionadas → evaluar
        if (afterFlip.phase == GamePhase.CHECKING) {
            evaluateMatch(afterFlip)
        }
    }

    // ── Lógica privada ───────────────────────────────────────────────────────

    private fun evaluateMatch(state: GameState) {
        viewModelScope.launch {
            delay(800L) // pausa para que el jugador vea las dos tarjetas
            when (checkMatch(state)) {
                MatchResult.HIT -> {
                    val newState = applyMatch(state)
                    _state.value = newState
                    _effects.send(GameEffect.HapticMatch)
                    if (newState.isComplete) onGameWon(newState)
                }
                MatchResult.MISS -> {
                    _state.value = flipBothBack(state)
                    _effects.send(GameEffect.HapticMiss)
                }
                MatchResult.PENDING -> Unit
            }
        }
    }

    /** Marca las dos tarjetas seleccionadas como encontradas (isMatched = true). */
    private fun applyMatch(state: GameState): GameState {
        val first  = state.firstSelected!!
        val second = state.secondSelected!!
        val newBoard = state.board.mapIndexed { i, card ->
            if (i == first || i == second) card.copy(isMatched = true) else card
        }
        val newMatchesFound = state.matchesFound + 1
        return state.copy(
            board          = newBoard,
            matchesFound   = newMatchesFound,
            firstSelected  = null,
            secondSelected = null,
            phase          = if (newMatchesFound == GameState.TOTAL_PAIRS)
                                 GamePhase.WON
                             else GamePhase.SELECTING_FIRST
        )
    }

    /** Voltea ambas tarjetas boca abajo después de un error. */
    private fun flipBothBack(state: GameState): GameState {
        val first  = state.firstSelected!!
        val second = state.secondSelected!!
        val newBoard = state.board.mapIndexed { i, c ->
            if (i == first || i == second) c.copy(isFlipped = false) else c
        }
        return state.copy(
            board          = newBoard,
            firstSelected  = null,
            secondSelected = null,
            phase          = GamePhase.SELECTING_FIRST
        )
    }

    /** Inicia el cronómetro (+1 segundo cada segundo). */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    /** Cancela el timer, guarda el mejor tiempo y envía vibración de victoria. */
    private suspend fun onGameWon(state: GameState) {
        timerJob?.cancel()
        saveBestTime(state.elapsedSeconds)
        _effects.send(GameEffect.HapticVictory)
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

// ── Efectos de una sola vez (vibración háptica) ─────────────────────────────
sealed class GameEffect {
    object HapticMatch   : GameEffect()
    object HapticMiss    : GameEffect()
    object HapticVictory : GameEffect()
}
