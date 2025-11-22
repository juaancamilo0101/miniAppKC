package com.karla.recetas.repo;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\nJ\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u00062\u0006\u0010\f\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/karla/recetas/repo/GeminiRepository;", "", "()V", "client", "Lokhttp3/OkHttpClient;", "generate", "Lcom/karla/recetas/repo/Recipe;", "baseIngredient", "", "protein", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "parseResponse", "jsonBody", "app_debug"})
public final class GeminiRepository {
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    
    public GeminiRepository() {
        super();
    }
    
    /**
     * Generates a recipe using Gemini API.
     * Logs errors to Logcat with tag "GEMINI_REPO".
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object generate(@org.jetbrains.annotations.NotNull()
    java.lang.String baseIngredient, @org.jetbrains.annotations.NotNull()
    java.lang.String protein, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.karla.recetas.repo.Recipe> $completion) {
        return null;
    }
    
    private final com.karla.recetas.repo.Recipe parseResponse(java.lang.String jsonBody) {
        return null;
    }
}