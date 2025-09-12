package com.nishtha.PrivateRepoReadMeGen.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiClient {

    @Value("${llm.endpoint}")
    private String llmEndpoint;

    @Value("${llm.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGemini(String prompt) {
        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt))
        );

        Map<String, Object> body = Map.of("contents", List.of(userContent));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                llmEndpoint,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getBody() != null) {
            try {
                List candidates = (List) response.getBody().get("candidates");
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                Map firstPart = (Map) parts.get(0);
                return (String) firstPart.get("text");
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse Gemini response: " + response.getBody(), e);
            }
        }
        return "No response from Gemini.";
    }
}

