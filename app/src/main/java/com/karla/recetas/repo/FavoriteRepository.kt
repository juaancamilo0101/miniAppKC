package com.karla.recetas.repo

import android.content.Context
import com.karla.recetas.data.AppDatabase
import com.karla.recetas.data.RecipeEntity

class FavoriteRepository(private val ctx: Context) {
  private val dao = AppDatabase.get(ctx).recipeDao()
  suspend fun save(entity: RecipeEntity) { dao.insert(entity) }
  suspend fun all(): List<RecipeEntity> = dao.getAll()
}