package com.karla.recetas.repo

import android.util.Log
import com.karla.recetas.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "AI_REPO"
private const val CONTENT_TYPE = "application/json; charset=utf-8"

/**
 * Modelo de datos para representar una receta.
 */
data class Recipe(val title: String, val ingredients: String, val steps: String)

/**
 * Repositorio encargado de la comunicación con OpenAI para generar recetas.
 */
class AIRepository {

    private val client = OkHttpClient()

    /**
     * Genera una receta enviando una solicitud a OpenAI.
     */
    suspend fun generate(baseIngredient: String, protein: String): Recipe? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.OPENAI_API_KEY
                if (apiKey.isBlank()) {
                    Log.e(TAG, "Error: API key de OpenAI está vacía")
                    return@withContext null
                }

                val prompt = "Genera una receta breve con $baseIngredient y $protein. " +
                        "Devuelve nombre, ingredientes y pasos en texto claro, separados por líneas."

                // Construimos el JSON de la petición según OpenAI Chat Completions
                val messages = JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put("content", prompt)
                )

                val rootJson = JSONObject()
                    .put("model", "gpt-3.5-turbo") // Modelo a usar
                    .put("messages", messages)

                val body = rootJson.toString().toRequestBody(CONTENT_TYPE.toMediaType())

                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    Log.e(TAG, "Error en la petición OpenAI: ${response.code} / $responseBody")
                    return@withContext null
                }

                parseResponse(responseBody)

            } catch (e: Exception) {
                Log.e(TAG, "Excepción al generar receta: ${e.message}")
                null
            }
        }
    }

    /**
     * Parsea la respuesta JSON de OpenAI para extraer la receta.
     */
    private fun parseResponse(jsonBody: String): Recipe? {
        try {
            val root = JSONObject(jsonBody)
            val choices = root.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null

            val text = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

            val title = lines.firstOrNull() ?: "Receta Generada"
            val ingredients = lines.drop(1)
                .takeWhile { !it.contains("paso", ignoreCase = true) && !it.contains("preparación", ignoreCase = true) }
                .joinToString("\n")
            val stepsStartIndex = 1 + ingredients.lines().size
            val steps = if (stepsStartIndex < lines.size) {
                lines.drop(stepsStartIndex).joinToString("\n")
            } else {
                "Sigue las instrucciones generales."
            }

            return Recipe(title, ingredients, steps)

        } catch (e: Exception) {
            Log.e(TAG, "Error parseando respuesta OpenAI: ${e.message}")
            return null
        }
    }
}
