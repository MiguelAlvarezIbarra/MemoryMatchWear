package mx.utng.memorymatch.presentation.board

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import mx.utng.memorymatch.domain.model.Card

/**
 * Composable de una tarjeta individual con animación de flip 3D.
 *
 * Cómo funciona el flip:
 * - rotationY va de 0° (dorso) a 180° (cara revelada)
 * - Cuando rotation > 90° mostramos la cara frontal
 * - Los textos llevan graphicsLayer { rotationY = 180f } para
 *   "desespejarse" y leerse correctamente tras la rotación del contenedor
 */
@Composable
fun CardItem(
    card: Card,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animar la rotación Y: 0° = boca abajo, 180° = revelada
    val rotation by animateFloatAsState(
        targetValue  = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label        = "cardFlip_${card.id}"
    )

    // La cara frontal es visible cuando la rotación superó el punto medio
    val isFrontVisible = rotation > 90f

    Box(
        modifier = modifier
            .size(52.dp)
            .graphicsLayer {
                rotationY     = rotation
                cameraDistance = 12f * density  // profundidad del efecto 3D
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    card.isMatched    -> Color(0xFF1B5E20)          // verde oscuro = encontrada
                    isFrontVisible    -> Color(card.symbol.color)    // color del concepto Android
                    else              -> Color(0xFF1A237E)           // azul oscuro = dorso
                }
            )
            .clickable(enabled = !card.isMatched) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        if (isFrontVisible) {
            // ── Cara revelada: emoji grande + etiqueta del concepto ──────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // graphicsLayer { rotationY = 180f } compensa la rotación del contenedor
                Text(
                    text     = card.symbol.emoji,
                    fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
                Text(
                    text     = card.symbol.label,
                    fontSize = 7.sp,
                    color    = Color.White,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        } else {
            // ── Dorso: logo UTNG ─────────────────────────────────────────────
            Text(
                text       = "U",
                color      = Color(0xFFF9A825),
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}
