package com.nishtha.PrivateRepoReadMeGen.service;

import com.nishtha.PrivateRepoReadMeGen.client.GeminiClient;
import org.springframework.stereotype.Service;

@Service
public class GenerateService {

    private final GeminiClient gemini;

    public GenerateService(GeminiClient gemini) {
        this.gemini = gemini;
    }

    public String generateReadme(String repoName, String content) {

        String prompt = "Write a professional README.md for the given repository.Read and understand the code before giving the output\n" +
                "\n" +
                "Include:\n" +
                "- Project title of the repository\n" +
                "- Description of what the code is trying to build\n" +
                "- Features written in the code for the project\n" +
                "- Tech stack used in the project\n" +
                "- Installation steps(easy to understand)\n" +
                "- Usage of the project\n" +
                "\n" +
                "Do NOT copy existing text.\n" +
                "Rewrite everything creatively but keep it simple and easy to understand.\n" +
                "\n" +
                "Project info: "

                + repoName + "\n\nCode/README:\n" + content;

        return gemini.generate(prompt);
    }
}