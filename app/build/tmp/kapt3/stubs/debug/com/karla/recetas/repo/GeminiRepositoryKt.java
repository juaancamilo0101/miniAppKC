package com.karla.recetas.repo;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0016\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u000e\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u000f\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0010\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0011\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0012\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0013\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0014\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0015\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0016\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"BASE_URL", "", "CONTENT_TYPE", "DEBUG_CODE", "DEBUG_KEY_LENGTH", "DEBUG_SENDING", "DEFAULT_TITLE", "ERROR_EMPTY_BODY", "ERROR_EMPTY_KEY", "ERROR_EXCEPTION", "ERROR_NO_CANDIDATES", "ERROR_PARSE", "ERROR_REQUEST_FAILED", "INSTRUCTION_FALLBACK", "JSON_KEY_CANDIDATES", "JSON_KEY_CONTENT", "JSON_KEY_CONTENTS", "JSON_KEY_PARTS", "JSON_KEY_TEXT", "KEYWORD_PREP", "KEYWORD_STEP", "PROMPT_TEMPLATE", "TAG", "app_debug"})
public final class GeminiRepositoryKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "GEMINI_REPO";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-001:generateContent";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CONTENT_TYPE = "application/json";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String JSON_KEY_TEXT = "text";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String JSON_KEY_PARTS = "parts";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String JSON_KEY_CONTENTS = "contents";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String JSON_KEY_CANDIDATES = "candidates";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String JSON_KEY_CONTENT = "content";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PROMPT_TEMPLATE = "Genera una receta breve con %s y %s. Devuelve nombre, ingredientes y pasos en texto claro.";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_EMPTY_KEY = "Error: La API Key est\u00e1 vac\u00eda. Revisa local.properties.";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_REQUEST_FAILED = "La petici\u00f3n fall\u00f3. Cuerpo de error: %s";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_EMPTY_BODY = "El cuerpo de la respuesta est\u00e1 vac\u00edo";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_NO_CANDIDATES = "No se encontraron candidatos en el JSON.";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_PARSE = "Error de parseo: %s";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ERROR_EXCEPTION = "Excepci\u00f3n: %s";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEBUG_KEY_LENGTH = "Longitud de API Key: %d";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEBUG_SENDING = "Enviando petici\u00f3n a: %s";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEBUG_CODE = "C\u00f3digo de Respuesta: %d";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEFAULT_TITLE = "Receta Generada";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String INSTRUCTION_FALLBACK = "Sigue las instrucciones generales.";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEYWORD_STEP = "paso";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEYWORD_PREP = "preparaci\u00f3n";
}