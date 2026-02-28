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

private const val TAG = "Groq_Repo"
private const val CONTENT_TYPE = "application/json; charset=utf-8"

/**
 * Modelo de datos para representar una receta.
 */
data class Recipe(val title: String, val ingredients: String, val steps: String)

/**
 * Repositorio encargado de la comunicación con Groq (Llama 3) para generar recetas.
 */
class AIRepository {

    private val client = OkHttpClient()

    /**
     * Genera una receta enviando una solicitud a Groq.
     */
    suspend fun generate(baseIngredient: String, protein: String): Recipe? {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.OPENAI_API_KEY

                if (apiKey.isBlank()) {
                    Log.e(TAG, "Error: API key de Groq está vacía")
                    return@withContext null
                }

                // Prompt ajustado para Llama 3: Le pedimos estructura estricta para facilitar el parseo
                val prompt = "Eres un chef experto. Crea una receta usando obligatoriamente $baseIngredient y $protein. " +
                        "IMPORTANTE: Solo devuelve el contenido de la receta sin saludos ni texto extra. " +
                        "Formato estricto:\n" +
                        "1. Primera línea: Título de la receta.\n" +
                        "2. Luego lista de ingredientes.\n" +
                        "3. Luego escribe la palabra 'PREPARACIÓN' en mayúsculas y lista los pasos."

                // Construimos el JSON. Groq es compatible con el formato de OpenAI.
                val messages = JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put("content", prompt)
                )

                val rootJson = JSONObject()
                    .put("model", "llama-3.3-70b-versatile") // Modelo Llama 3 en Groq
                    .put("messages", messages)
                    .put("temperature", 0.5) // Creatividad balanceada

                val body = rootJson.toString().toRequestBody(CONTENT_TYPE.toMediaType())

                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions") // Endpoint de Groq
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    Log.e(TAG, "Error en la petición Groq: ${response.code} / $responseBody")
                    return@withContext null
                }

                // Usamos la misma lógica de parseo ya que el JSON de respuesta es idéntico
                parseResponse(responseBody)

            } catch (e: Exception) {
                Log.e(TAG, "Excepción al generar receta: ${e.message}")
                null
            }
        }
    }

    /**
     * Parsea la respuesta JSON de Groq para extraer la receta.
     */
    private fun parseResponse(jsonBody: String): Recipe? {
        try {
            val root = JSONObject(jsonBody)
            val choices = root.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null

            var text = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            // Limpieza básica por si la IA pone comillas o espacios extra
            text = text.trim().removePrefix("\"").removeSuffix("\"")

            val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

            if (lines.isEmpty()) return null

            // 1. Título (Primera línea)
            val title = lines.firstOrNull()?.replace("**", "") ?: "Receta Generada"

            // 2. Ingredientes (Desde la línea 2 hasta encontrar 'PREPARACIÓN' o 'PASOS')
            val ingredients = lines.drop(1)
                .takeWhile {
                    !it.contains("PREPARACIÓN", ignoreCase = true) &&
                            !it.contains("PASOS", ignoreCase = true) &&
                            !it.contains("INSTRUCCIONES", ignoreCase = true)
                }
                .joinToString("\n")

            // 3. Pasos (El resto del texto)
            val stepsStartIndex = 1 + ingredients.lines().size
            // Buscamos si hay cabecera de pasos para saltarla también
            val stepsRaw = if (stepsStartIndex < lines.size) {
                lines.drop(stepsStartIndex)
            } else {
                emptyList()
            }

            // Quitamos la palabra "PREPARACIÓN" si se coló en la primera línea de los pasos
            val steps = stepsRaw.filter {
                !it.equals("PREPARACIÓN", ignoreCase = true) &&
                        !it.equals("PASOS", ignoreCase = true)
            }.joinToString("\n")

            return Recipe(title, ingredients, steps)

        } catch (e: Exception) {
            Log.e(TAG, "Error parseando respuesta Groq: ${e.message}")
            return null
        }
    }
}