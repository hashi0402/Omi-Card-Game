package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing official Sri Lankan Omi rules and scoring logic.
 */
@Entity(tableName = "omi_rules")
data class OmiRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String, // "DECK", "DEAL", "TRUMP", "PLAY", "KAPOTHI", "KASSAPUWA", "TOKENS"
    val titleEn: String,
    val titleSi: String,
    val summaryEn: String,
    val summarySi: String,
    val detailsEn: String,
    val detailsSi: String,
    val tokenValue: Int = 0,
    val orderIndex: Int = 0
)
