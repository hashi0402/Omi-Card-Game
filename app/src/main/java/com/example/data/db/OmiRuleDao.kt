package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OmiRuleDao {

    @Query("SELECT * FROM omi_rules ORDER BY orderIndex ASC")
    fun getAllRules(): Flow<List<OmiRuleEntity>>

    @Query("SELECT * FROM omi_rules WHERE category = :category ORDER BY orderIndex ASC")
    fun getRulesByCategory(category: String): Flow<List<OmiRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<OmiRuleEntity>)

    @Query("SELECT COUNT(*) FROM omi_rules")
    suspend fun getRulesCount(): Int

    @Query("DELETE FROM omi_rules")
    suspend fun clearRules()
}
