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

@RestController
@RequestMapping("/private-repo")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class RepoController {

    private final GitHubService gitHubService;
    private final GenerateService generateService;

    public RepoController(GitHubService gitHubService, GenerateService generateService) {
        this.gitHubService = gitHubService;
        this.generateService = generateService;
    }

    @GetMapping
    public List<RepoDTO> getRepos(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client
    ) {
        return gitHubService.getRepos(client);
    }

    @PostMapping("/{owner}/{repo}/generate-readme")
    public String generate(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client,
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestBody ReadMeDTO request
    ) {
        String readme = gitHubService.getRepoReadme(client, owner, repo);
        return generateService.generateReadme(
                repo,
                readme + "\n\n" + request.getSnippet());
    }
}