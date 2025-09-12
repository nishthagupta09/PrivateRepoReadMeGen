package com.nishtha.PrivateRepoReadMeGen.service;

import com.nishtha.PrivateRepoReadMeGen.client.GeminiClient;
import org.springframework.stereotype.Service;

@Service
public class GenerateService {

    private final GeminiClient geminiClient;

    public GenerateService(GeminiClient llmClient) {
        this.geminiClient = llmClient;
    }

    public String generateReadme(String username, String repo, String snippet) {
        String prompt = """
        You are a helpful assistant that generates professional README files.
        The README should be in clean Markdown format and always follow this structure:

        1. Project Title
        2. Description
        3. Features
        4. Installation Guide
        5. Tech Stack
        6. Project Structure
        7. License Information

        Repository name: %s/%s
        Optional code snippet for context:
        %s

        Write the README now.
        """.formatted(username, repo, snippet == null ? "" : snippet);

        return geminiClient.askGemini(prompt);
    }
}
