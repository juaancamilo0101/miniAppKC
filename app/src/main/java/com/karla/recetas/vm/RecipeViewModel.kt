package com.karla.recetas.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karla.recetas.data.RecipeEntity
import com.karla.recetas.repo.FavoriteRepository
import com.karla.recetas.repo.GeminiRepository
import kotlinx.coroutines.launch

/**
 * ViewModel to manage recipe generation and favorites.
 */
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

  private val favoriteRepository = FavoriteRepository(application)
  private val geminiRepository = GeminiRepository()

  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean> = _loading

  private val _recipe = MutableLiveData<RecipeEntity?>(null)
  val recipe: LiveData<RecipeEntity?> = _recipe

  private val _favorites = MutableLiveData<List<RecipeEntity>>(emptyList())
  val favorites: LiveData<List<RecipeEntity>> = _favorites

  private val _error = MutableLiveData<String?>(null)
  val error: LiveData<String?> = _error

  private var firstIngredient: String? = null
  private var secondIngredient: String? = null

  /**
   * Updates the selected ingredients.
   * @param optionNumber 1 for the first ingredient, 2 for the second.
   * @param value The name of the ingredient.
   */
  fun setChoice(optionNumber: Int, value: String) {
    if (optionNumber == 1) {
      firstIngredient = value
    } else {
      secondIngredient = value
    }
  }

  /**
   * Calls the repository to generate a recipe using Gemini.
   */
  fun generateRecipe() {
    _loading.value = true

    // We launch in viewModelScope. The Repository handles the IO switching.
    viewModelScope.launch {
      // Default values if null, avoiding hardcoded + concatenation
      val ingredient1 = firstIngredient ?: "Arroz"
      val ingredient2 = secondIngredient ?: "Pollo"

      val result = geminiRepository.generate(ingredient1, ingredient2)

      if (result == null) {
        _error.postValue("Sin conexión o error con la API")
        _loading.postValue(false)
      } else {
        val entity = RecipeEntity(
          title = result.title,
          ingredients = result.ingredients,
          steps = result.steps
        )
        _recipe.postValue(entity)
        _error.postValue(null)
        _loading.postValue(false)
      }
    }
  }

  /**
   * Saves the current recipe to the local database.
   */
  fun saveFavorite() {
    val currentRecipe = _recipe.value ?: return
    viewModelScope.launch {
      favoriteRepository.save(currentRecipe)
    }
  }

  /**
   * Loads all favorite recipes from the database.
   */
  fun loadFavorites() {
    viewModelScope.launch {
      val list = favoriteRepository.all()
      _favorites.postValue(list)
    }
  }
}