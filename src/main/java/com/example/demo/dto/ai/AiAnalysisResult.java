package com.example.demo.dto.ai;

public record AiAnalysisResult(String summary, String priority, String suggestedResolution) {

    // Safe default when Ollama is unavailable, times out, or returns invalid JSON.
    public static AiAnalysisResult fallback() {
        return new AiAnalysisResult(null, "MEDIUM", null);
    }
}
