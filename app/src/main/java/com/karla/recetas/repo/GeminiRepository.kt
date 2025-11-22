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

data class Recipe(val title: String, val ingredients: String, val steps: String)

class GeminiRepository {

    private val client = OkHttpClient()

    /**
     * Generates a recipe using Gemini API.
     * Logs errors to Logcat with tag "GEMINI_REPO".
     */
    suspend fun generate(baseIngredient: String, protein: String): Recipe? {
        return withContext(Dispatchers.IO) {
            val key = BuildConfig.GEMINI_API_KEY

            // Debug: Check if key is loaded
            Log.d("GEMINI_REPO", "API Key length: ${key?.length ?: 0}")

            if (key.isNullOrBlank()) {
                Log.e("GEMINI_REPO", "Error: API Key is empty. Check local.properties.")
                return@withContext null
            }


            // Fix: Usamos la versión base exacta 'gemini-1.5-flash' (sin 'latest' ni 'pro')
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$key"
            val prompt =
                "Genera una receta breve con $baseIngredient y $protein. Devuelve nombre, ingredientes y pasos en texto claro."

            try {
                val textPart = JSONObject().put("text", prompt)
                val partsArray = JSONArray().put(textPart)
                val contentObj = JSONObject().put("parts", partsArray)
                val contentsArray = JSONArray().put(contentObj)
                val rootJson = JSONObject().put("contents", contentsArray)

                val body = rootJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                Log.d("GEMINI_REPO", "Sending request to: $url")

                val response = client.newCall(request).execute()
                Log.d("GEMINI_REPO", "Response Code: ${response.code}")

                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    Log.e("GEMINI_REPO", "Request failed. Body: $errorBody")
                    response.close()
                    return@withContext null
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrBlank()) {
                    Log.e("GEMINI_REPO", "Response body is empty")
                    return@withContext null
                }

                // Parse response
                parseResponse(responseBody)

            } catch (e: Exception) {
                Log.e("GEMINI_REPO", "Exception: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }

    private fun parseResponse(jsonBody: String): Recipe? {
        try {
            val root = JSONObject(jsonBody)
            val candidates = root.optJSONArray("candidates")

            if (candidates == null || candidates.length() == 0) {
                Log.e("GEMINI_REPO", "No candidates found in JSON.")
                return null
            }

            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null

            val fullText = buildString {
                for (i in 0 until parts.length()) {
                    append(parts.getJSONObject(i).optString("text"))
                }
            }

            val lines = fullText.split("\n").filter { it.isNotBlank() }
            val title = lines.firstOrNull() ?: "Receta Generada"

            val ingredients = lines.drop(1)
                .takeWhile {
                    val line = it.lowercase()
                    !line.contains("paso") && !line.contains("preparación")
                }
                .joinToString("\n")

            val stepsStartIndex = 1 + ingredients.split("\n").size
            val steps = if (stepsStartIndex < lines.size) {
                lines.drop(stepsStartIndex).joinToString("\n")
            } else {
                "Sigue las instrucciones generales."
            }

            return Recipe(title, ingredients, steps)
        } catch (e: Exception) {
            Log.e("GEMINI_REPO", "Parse Error: ${e.message}")
            return null
        }
    }
}