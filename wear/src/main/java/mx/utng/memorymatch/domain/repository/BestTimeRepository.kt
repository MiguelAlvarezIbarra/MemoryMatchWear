package mx.utng.memorymatch.domain.repository

/**
 * Interfaz del dominio — no depende de Android ni DataStore.
 * Regla de Dependencia: el dominio define la abstracción,
 * la capa de datos la implementa.
 */
interface BestTimeRepository {
    suspend fun getBestTime(): Long
    suspend fun saveBestTime(seconds: Long)
}
