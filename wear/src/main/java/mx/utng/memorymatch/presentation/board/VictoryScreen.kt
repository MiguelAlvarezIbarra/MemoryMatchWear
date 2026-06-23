package mx.utng.memorymatch.presentation.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import mx.utng.memorymatch.domain.model.GameState

/**
 * Pantalla que se muestra cuando el jugador completa todos los pares.
 * Muestra el tiempo, los movimientos y si batió el récord anterior.
 */
@Composable
fun VictoryScreen(
    state: GameState,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A2A0A)),   // fondo verde muy oscuro
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Título ────────────────────────────────────────────────────────
            Text(
                text       = "🏆 ¡Completado!",
                style      = MaterialTheme.typography.title3,
                color      = Color(0xFFF9A825),   // dorado
                fontWeight = FontWeight.Bold
            )

            // ── Estadísticas de la partida ────────────────────────────────────
            Text(
                text  = "⏱ ${state.elapsedSeconds}s · 🎯 ${state.moves} mov",
                style = MaterialTheme.typography.body2,
                color = Color.White
            )

            // ── Mejor tiempo / nuevo récord ───────────────────────────────────
            if (state.bestTime < Long.MAX_VALUE) {
                val isNewRecord = state.elapsedSeconds <= state.bestTime
                Text(
                    text  = if (isNewRecord) "⭐ ¡Nuevo récord!"
                            else "🏅 Mejor: ${state.bestTime}s",
                    color = Color(0xFFA5D6A7),    // verde claro
                    style = MaterialTheme.typography.caption2
                )
            }

            // ── Botón para jugar de nuevo ─────────────────────────────────────
            Chip(
                onClick  = onRestart,
                label    = { Text("↺ Jugar de nuevo") },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}
