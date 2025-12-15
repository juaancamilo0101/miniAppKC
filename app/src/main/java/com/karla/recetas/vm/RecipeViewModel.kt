package com.karla.recetas.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.karla.recetas.BuildConfig
import com.karla.recetas.data.RecipeEntity
import com.karla.recetas.repo.AIRepository
import com.karla.recetas.repo.FavoriteRepository
import kotlinx.coroutines.launch

private const val DEFAULT_INGREDIENT_1 = "Arroz"
private const val DEFAULT_INGREDIENT_2 = "Pollo"
private const val ERROR_MSG_API = "Sin conexión o error con la API"

/**
 * ViewModel para gestionar la generación de recetas y favoritos.
 */
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

  private val favoriteRepository = FavoriteRepository(application)

  val apiKey = BuildConfig.OPENAI_API_KEY


  private val geminiRepository = AIRepository()

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
   * Actualiza los ingredientes seleccionados.
   * @param optionNumber 1 para el primer ingrediente, 2 para el segundo.
   * @param value El nombre del ingrediente.
   */
  fun setChoice(optionNumber: Int, value: String) {
    if (optionNumber == 1) {
      firstIngredient = value
    } else {
      secondIngredient = value
    }
  }

  /**
   * Llama al repositorio para generar una receta usando Gemini.
   */
  fun generateRecipe() {
    _loading.value = true

    viewModelScope.launch {
      // Validamos que no sean nulos ni vacios (usando isNullOrBlank de Kotlin que es similar a isBlank)
      val ingredient1 = if (firstIngredient.isNullOrBlank()) DEFAULT_INGREDIENT_1 else firstIngredient!!
      val ingredient2 = if (secondIngredient.isNullOrBlank()) DEFAULT_INGREDIENT_2 else secondIngredient!!

      try {
        val result = geminiRepository.generate(ingredient1, ingredient2)

        if (result == null) {
          _error.postValue(ERROR_MSG_API)
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
      } catch (e: Exception) {
        // Capturamos cualquier error que venga del repositorio
        _error.postValue(e.message)
        _loading.postValue(false)
      }
    }
  }

  /**
   * Guarda la receta actual en la base de datos local.
   */
  fun saveFavorite() {
    val currentRecipe = _recipe.value ?: return
    viewModelScope.launch {
      favoriteRepository.save(currentRecipe)
    }
  }

  /**
   * Carga todas las recetas favoritas de la base de datos.
   */
  fun loadFavorites() {
    viewModelScope.launch {
      val list = favoriteRepository.all()
      _favorites.postValue(list)
    }
  }
}