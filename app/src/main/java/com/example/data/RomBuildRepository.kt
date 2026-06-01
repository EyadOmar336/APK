package com.example.data

import kotlinx.coroutines.flow.Flow

class RomBuildRepository(private val romBuildDao: RomBuildDao) {
    val allBuilds: Flow<List<RomBuild>> = romBuildDao.getAllBuilds()

    suspend fun insert(build: RomBuild): Long {
        return romBuildDao.insertBuild(build)
    }

    suspend fun deleteById(id: Int) {
        romBuildDao.deleteBuildById(id)
    }

    suspend fun clearHistory() {
        romBuildDao.clearHistory()
    }
}
