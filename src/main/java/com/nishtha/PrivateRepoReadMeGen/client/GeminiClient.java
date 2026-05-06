package com.nishtha.PrivateRepoReadMeGen.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiClient {

    @Value("${llm.endpoint}")
    private String endpoint;

    @Value("${llm.api-key}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();

    public String generate(String prompt) {

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = endpoint + "?key=" + apiKey;

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        System.out.println("GEMINI URL => " + url);

        ResponseEntity<Map> res = rest.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        System.out.println("FULL GEMINI RESPONSE: " + res.getBody());

        try{

            Map<String, Object> responseBody = res.getBody();

            if (responseBody == null || !responseBody.containsKey("candidates")) {
                return "No response from Gemini.";
            }

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseBody.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "No content generated.";
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");

            if (content == null) {
                return "Gemini returned no content.";
            }

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "No text found.";
            }

            Map<String, Object> first = parts.get(0);

            return (String) first.get("text");

        } catch (Exception e) {
            return "Failed to parse Gemini response: " + e.getMessage();
        }
    }
}