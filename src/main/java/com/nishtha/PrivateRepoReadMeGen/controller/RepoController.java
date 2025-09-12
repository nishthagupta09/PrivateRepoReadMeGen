package com.nishtha.PrivateRepoReadMeGen.controller;

import com.nishtha.PrivateRepoReadMeGen.DTOs.ReadMeDTO;
import com.nishtha.PrivateRepoReadMeGen.DTOs.RepoDTO;
import com.nishtha.PrivateRepoReadMeGen.service.GenerateService;
import com.nishtha.PrivateRepoReadMeGen.service.GitHubService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/private-repo")
public class RepoController {

    private final GitHubService gitHubService;
    private final GenerateService geminiService;

    public RepoController(GitHubService gitHubService, GenerateService geminiService) {
        this.gitHubService = gitHubService;
        this.geminiService = geminiService;
    }

    @GetMapping
    public List<RepoDTO> listRepos(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {
        return gitHubService.listRepos(client);
    }

   @PostMapping("/{username}/{repo}/generate-readme")
   public String generateReadme(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
                                   @PathVariable String username,
                                   @PathVariable String repo,
                                    @RequestBody Map<String, String> body) {
       String codeSnippet = body.getOrDefault("snippet", "");
       String repoReadme = gitHubService.getRepoReadme(client, username, repo);
       return geminiService.generateReadme(repo, repoReadme, codeSnippet);
    }

}
