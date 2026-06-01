package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rom_builds")
data class RomBuild(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val buildName: String,
    val ramGb: Int,
    val storageGb: Int,
    val playServices: Boolean,
    val kernel: String,
    val bundledApps: String, // Comma separated, e.g., "Browser, File Manager"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String // "COMPLETED" or "FAILED" or "QUEUED"
)
