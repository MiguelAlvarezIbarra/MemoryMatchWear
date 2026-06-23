package mx.utng.memorymatch.data.repository

import mx.utng.memorymatch.data.datasource.BestTimeDataSource
import mx.utng.memorymatch.domain.repository.BestTimeRepository

/**
 * Implementación concreta del repositorio.
 * Vive en la capa de datos — depende de BestTimeDataSource.
 * El dominio solo conoce la interfaz BestTimeRepository.
 */
class BestTimeRepositoryImpl(
    private val dataSource: BestTimeDataSource
) : BestTimeRepository {

    override suspend fun getBestTime(): Long =
        dataSource.getBestTime()

    override suspend fun saveBestTime(seconds: Long) =
        dataSource.saveBestTime(seconds)
}
