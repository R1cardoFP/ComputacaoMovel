package com.example.trabalhocm.data.local.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_results")
data class OfflineResultEntity(
    @PrimaryKey(autoGenerate = true)
    val idLocal: Long = 0,

    val nomeJogo: String,

    val resultadoEquipaA: Int,

    val resultadoEquipaB: Int,

    val observacoes: String,

    val dataRegisto: Long = System.currentTimeMillis(),

    val sincronizado: Boolean = false
)