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


private const val TAG = "GEMINI_REPO"
// CAMBIO IMPORTANTE: Usamos la versión específica -001 para evitar el error 404
private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-001:generateContent"
private const val CONTENT_TYPE = "application/json"
private const val JSON_KEY_TEXT = "text"
private const val JSON_KEY_PARTS = "parts"
private const val JSON_KEY_CONTENTS = "contents"
private const val JSON_KEY_CANDIDATES = "candidates"
private const val JSON_KEY_CONTENT = "content"
private const val PROMPT_TEMPLATE = "Genera una receta breve con %s y %s. Devuelve nombre, ingredientes y pasos en texto claro."
private const val ERROR_EMPTY_KEY = "Error: La API Key está vacía. Revisa local.properties."
private const val ERROR_REQUEST_FAILED = "La petición falló. Cuerpo de error: %s"
private const val ERROR_EMPTY_BODY = "El cuerpo de la respuesta está vacío"
private const val ERROR_NO_CANDIDATES = "No se encontraron candidatos en el JSON."
private const val ERROR_PARSE = "Error de parseo: %s"
private const val ERROR_EXCEPTION = "Excepción: %s"
private const val DEBUG_KEY_LENGTH = "Longitud de API Key: %d"
private const val DEBUG_SENDING = "Enviando petición a: %s"
private const val DEBUG_CODE = "Código de Respuesta: %d"
private const val DEFAULT_TITLE = "Receta Generada"
private const val INSTRUCTION_FALLBACK = "Sigue las instrucciones generales."
private const val KEYWORD_STEP = "paso"
private const val KEYWORD_PREP = "preparación"

/**
 * Modelo de datos para representar una receta.
 */
data class Recipe(val title: String, val ingredients: String, val steps: String)

/**
 * Repositorio encargado de la comunicación con la API de Gemini.
 * Gestiona la llamada de red y el parseo de la respuesta.
 */
class GeminiRepository {

    private val client = OkHttpClient()

    /**
     * Genera una receta enviando una solicitud a la API de Gemini.
     * @param baseIngredient Ingrediente base (ej. Arroz).
     * @param protein Proteína o segundo ingrediente (ej. Pollo).
     * @return Objeto Recipe o null si falla.
     */
    suspend fun generate(baseIngredient: String, protein: String): Recipe? {
        return withContext(Dispatchers.IO) {
            val key = BuildConfig.GEMINI_API_KEY

            val keyLength = key?.length ?: 0
            Log.d(TAG, String.format(DEBUG_KEY_LENGTH, keyLength))

            // Validación estricta sin "== null"
            if (key.isNullOrBlank()) {
                Log.e(TAG, ERROR_EMPTY_KEY)
                return@withContext null
            }

            // Usamos template string en lugar de concatenación con +
            val url = "$BASE_URL?key=$key"

            // Formateamos el prompt de manera segura
            val prompt = String.format(PROMPT_TEMPLATE, baseIngredient, protein)

            try {
                // Construcción del JSON usando objetos nativos para evitar errores de string manual
                val textPart = JSONObject().put(JSON_KEY_TEXT, prompt)
                val partsArray = JSONArray().put(textPart)
                val contentObj = JSONObject().put(JSON_KEY_PARTS, partsArray)
                val contentsArray = JSONArray().put(contentObj)
                val rootJson = JSONObject().put(JSON_KEY_CONTENTS, contentsArray)

                val body = rootJson.toString().toRequestBody(CONTENT_TYPE.toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                Log.d(TAG, String.format(DEBUG_SENDING, url))

                val response = client.newCall(request).execute()
                Log.d(TAG, String.format(DEBUG_CODE, response.code))

                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    Log.e(TAG, String.format(ERROR_REQUEST_FAILED, errorBody))
                    response.close()
                    return@withContext null
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrBlank()) {
                    Log.e(TAG, ERROR_EMPTY_BODY)
                    return@withContext null
                }

                parseResponse(responseBody)

            } catch (e: Exception) {
                Log.e(TAG, String.format(ERROR_EXCEPTION, e.message))
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Parsea la respuesta JSON para extraer el título, ingredientes y pasos.
     * @param jsonBody String con el JSON crudo de la respuesta.
     */
    private fun parseResponse(jsonBody: String): Recipe? {
        try {
            val root = JSONObject(jsonBody)
            val candidates = root.optJSONArray(JSON_KEY_CANDIDATES)

            // Validación de array vacío o nulo
            if (candidates == null || candidates.length() == 0) {
                Log.e(TAG, ERROR_NO_CANDIDATES)
                return null
            }

            val content = candidates.getJSONObject(0).optJSONObject(JSON_KEY_CONTENT) ?: return null
            val parts = content.optJSONArray(JSON_KEY_PARTS) ?: return null

            val fullText = buildString {
                for (i in 0 until parts.length()) {
                    append(parts.getJSONObject(i).optString(JSON_KEY_TEXT))
                }
            }

            val lines = fullText.split("\n").filter { it.isNotBlank() }
            val title = lines.firstOrNull() ?: DEFAULT_TITLE

            val ingredients = lines.drop(1)
                .takeWhile {
                    val line = it.lowercase()
                    !line.contains(KEYWORD_STEP) && !line.contains(KEYWORD_PREP)
                }
                .joinToString("\n")

            val stepsStartIndex = 1 + ingredients.split("\n").size
            val steps = if (stepsStartIndex < lines.size) {
                lines.drop(stepsStartIndex).joinToString("\n")
            } else {
                INSTRUCTION_FALLBACK
            }

            return Recipe(title, ingredients, steps)
        } catch (e: Exception) {
            Log.e(TAG, String.format(ERROR_PARSE, e.message))
            return null
        }
    }
}