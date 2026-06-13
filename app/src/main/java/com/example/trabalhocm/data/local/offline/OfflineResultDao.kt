package com.example.trabalhocm.data.local.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineResultDao {

    @Query("SELECT * FROM offline_results WHERE sincronizado = 0 ORDER BY dataRegisto DESC")
    fun observarPendentes(): Flow<List<OfflineResultEntity>>

    @Query("SELECT * FROM offline_results WHERE sincronizado = 0 ORDER BY dataRegisto ASC")
    suspend fun listarPendentesUmaVez(): List<OfflineResultEntity>

    @Insert
    suspend fun inserir(resultado: OfflineResultEntity)

    @Query("UPDATE offline_results SET sincronizado = 1 WHERE idLocal = :idLocal")
    suspend fun marcarComoSincronizado(idLocal: Long)

    @Query("DELETE FROM offline_results WHERE idLocal = :idLocal")
    suspend fun remover(idLocal: Long)
} 