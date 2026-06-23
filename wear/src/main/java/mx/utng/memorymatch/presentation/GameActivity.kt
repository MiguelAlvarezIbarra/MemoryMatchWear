package mx.utng.memorymatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import mx.utng.memorymatch.presentation.board.BoardScreen
import mx.utng.memorymatch.presentation.board.MemoryViewModel

/**
 * Punto de entrada de la aplicación.
 * Configura el tema Wear OS y lanza la pantalla del tablero.
 */
class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MemoryViewModel = viewModel(
                factory = MemoryViewModelFactory(applicationContext)
            )
            MaterialTheme {
                BoardScreen(viewModel = viewModel)
            }
        }
    }
}
