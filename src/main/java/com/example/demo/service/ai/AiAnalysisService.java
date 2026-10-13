package com.example.demo.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ai.AiAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AiAnalysisService.class);

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;

    public AiAnalysisService(OllamaClient ollamaClient, ObjectMapper objectMapper) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = objectMapper;
    }

    public AiAnalysisResult analyze(String title, String description) {
    try {
        String prompt = buildPrompt(title, description);

        System.out.println("========== AI ANALYSIS START ==========");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);

        String raw = ollamaClient.generate(prompt);

        System.out.println("Raw AI response: " + raw);
        //Then this line converts JSON into your Java object

        AiAnalysisResult result =
                objectMapper.readValue(raw, AiAnalysisResult.class);

        System.out.println("AI Summary: " + result.summary());
        System.out.println("AI Priority: " + result.priority());
        System.out.println("AI Suggested Resolution: " + result.suggestedResolution());
        System.out.println("========== AI ANALYSIS END ==========");

        return result;

    } catch (Exception e) {

        System.out.println("========== AI ANALYSIS ERROR ==========");
        e.printStackTrace();
        System.out.println("=======================================");

        log.warn("AI analysis unavailable, using fallback defaults: {}", e.toString());

        return AiAnalysisResult.fallback();
    }
}

    private String buildPrompt(String title, String description) {
        return """
            You are a customer support triage assistant.
            Respond ONLY with valid JSON, no extra commentary.

            Ticket Title: %s
            Ticket Description: %s

            JSON format:
            {"summary": "...", "priority": "LOW|MEDIUM|HIGH|CRITICAL", "suggestedResolution": "..."}
            """.formatted(title, description);
    }
}
