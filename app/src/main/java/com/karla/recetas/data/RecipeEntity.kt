package com.karla.recetas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas_favoritas")
data class RecipeEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val ingredients: String,
  val steps: String
)