package com.example.demo.service.ai;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final String model;
    private final ObjectMapper objectMapper;

    public OllamaClient(
            @Value("${ai.ollama.base-url}") String baseUrl,
            @Value("${ai.ollama.model}") String model,
            @Value("${ai.ollama.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${ai.ollama.read-timeout-ms}") int readTimeoutMs,
            ObjectMapper objectMapper) {

        this.model = model;
        this.objectMapper = objectMapper;

        ClientHttpRequestFactorySettings settings =
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                        .withReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactories.get(settings))
                .build();
    }
    //"OllamaClient, here is the prompt. Send it to Ollama and give me the response."

    public String generate(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "format", "json"
        );

        System.out.println("========== SENDING TO OLLAMA ==========");
        System.out.println("Model: " + model);
        System.out.println("Prompt: " + prompt);
        System.out.println("=======================================");

        // Get Ollama response as raw String
        //http://localhost:11434/api/generate
        String rawResponse = restClient.post()
                .uri("/api/generate")
                //Put this JSON data into the HTTP request body.
                .body(requestBody)
                //Send the request and get the response.
                .retrieve()
                //Give me the response body as a Java String.
                .body(String.class);

        System.out.println("========== OLLAMA RAW RESPONSE ==========");
        System.out.println(rawResponse);
        System.out.println("==========================================");

        try {
            // Convert raw JSON response into JsonNode
            //This converts the raw JSON string into a JSON structure that Java can navigate.
            //Jackson has understood the structure.
            JsonNode root = objectMapper.readTree(rawResponse);

            // Extract Ollama's "response" field
            //Give me the value inside Ollama's "response" field    
            //you get the inner content as a normal Java String:
            //{"summary":"Payment failed","priority":"CRITICAL","suggestedResolution":"Investigate payment system"}            
            String aiResponse = root.path("response").asText();

            System.out.println("========== EXTRACTED AI RESPONSE ==========");
            System.out.println(aiResponse);
            System.out.println("===========================================");

            return aiResponse;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse Ollama response: " + rawResponse, e);
        }
    }
}