package mx.utng.memorymatch.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "memory_match_prefs")

/**
 * Fuente de datos local usando DataStore Preferences.
 * Guarda y recupera el mejor tiempo (en segundos).
 */
class BestTimeDataSource(private val context: Context) {

    private object Keys {
        val BEST_TIME = longPreferencesKey("best_time_seconds")
    }

    suspend fun getBestTime(): Long =
        context.dataStore.data
            .map { prefs -> prefs[Keys.BEST_TIME] ?: Long.MAX_VALUE }
            .first()

    suspend fun saveBestTime(seconds: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BEST_TIME] ?: Long.MAX_VALUE
            // Solo guardar si es un tiempo mejor (menor)
            if (seconds < current) {
                prefs[Keys.BEST_TIME] = seconds
            }
        }
    }
}
