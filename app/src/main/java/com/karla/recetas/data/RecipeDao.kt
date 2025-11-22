package com.karla.recetas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
  @Insert
  suspend fun insert(entity: RecipeEntity)

  @Query("SELECT * FROM recetas_favoritas ORDER BY id DESC")
  suspend fun getAll(): List<RecipeEntity>
}