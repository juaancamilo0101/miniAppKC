package com.karla.recetas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun recipeDao(): RecipeDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null
    fun get(ctx: Context): AppDatabase = INSTANCE ?: synchronized(this) {
      INSTANCE ?: Room.databaseBuilder(ctx.applicationContext, AppDatabase::class.java, "recetas.db").build().also { INSTANCE = it }
    }
  }
}