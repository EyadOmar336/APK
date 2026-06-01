package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RomBuildDao {
    @Query("SELECT * FROM rom_builds ORDER BY timestamp DESC")
    fun getAllBuilds(): Flow<List<RomBuild>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: RomBuild): Long

    @Query("DELETE FROM rom_builds WHERE id = :id")
    suspend fun deleteBuildById(id: Int)

    @Query("DELETE FROM rom_builds")
    suspend fun clearHistory()
}
