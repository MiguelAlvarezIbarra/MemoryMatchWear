package mx.utng.memorymatch.presentation.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.scrollAway
import kotlinx.coroutines.delay
import mx.utng.memorymatch.domain.model.GamePhase

@Composable
fun BoardScreen(viewModel: MemoryViewModel = viewModel()) {
    val state    by viewModel.state.collectAsState()
    val haptic    = LocalHapticFeedback.current
    val listState = rememberScalingLazyListState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                GameEffect.HapticMatch   ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                GameEffect.HapticMiss    ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                GameEffect.HapticVictory -> repeat(3) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(150L)
                }
            }
        }
    }

    if (state.phase == GamePhase.WON) {
        VictoryScreen(state = state, onRestart = viewModel::startNewGame)
        return
    }

    Scaffold(
        timeText = {
            TimeText(
                modifier = Modifier.scrollAway(listState),
                endLinearContent = {
                    Text(
                        text  = "${state.elapsedSeconds}s · ${state.moves} mov",
                        style = MaterialTheme.typography.caption2
                    )
                }
            )
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A1E))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(top = 28.dp, start = 8.dp, end = 8.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement   = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(state.board) { index, card ->
                    CardItem(
                        card  = card,
                        onTap = { viewModel.onCardTapped(index) },
                    )
                }
            }
        }
    }
}