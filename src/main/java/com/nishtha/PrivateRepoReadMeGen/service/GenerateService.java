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

        String prompt = """
You are an expert software engineer.

Generate a professional README.md for this repository.

Analyze the repository structure carefully and infer:

- programming language
- framework
- architecture
- purpose of project
- likely features
- setup instructions
- usage instructions

Write a polished GitHub README.

Include:
# Project Title
# Description
# Features
# Tech Stack
# Project Structure
# Installation
# Usage
# API Endpoints (if backend)
# Future Improvements

Repository name:
""" + repoName + """

Repository structure:
""" + content;
        return gemini.generate(prompt);
    }
}