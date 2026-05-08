package com.nishtha.PrivateRepoReadMeGen.controller;

import com.nishtha.PrivateRepoReadMeGen.DTOs.ReadMeDTO;
import com.nishtha.PrivateRepoReadMeGen.DTOs.RepoDTO;
import com.nishtha.PrivateRepoReadMeGen.service.GenerateService;
import com.nishtha.PrivateRepoReadMeGen.service.GitHubService;
import com.nishtha.PrivateRepoReadMeGen.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private-repo")
@CrossOrigin(origins = "https://repo-read-me-gen.vercel.app")
public class RepoController {

    private final GitHubService gitHubService;
    private final GenerateService generateService;

    @Autowired
    private final JWTService JWTService;

    public RepoController(GitHubService gitHubService, GenerateService generateService, JWTService jwtService) {
        this.gitHubService = gitHubService;
        this.generateService = generateService;
        this.JWTService = jwtService;
    }

    @GetMapping
    public List<RepoDTO> getRepos(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token =
                authHeader.replace("Bearer ", "");

        String githubToken =
                JWTService.extractGithubToken(token);

        return gitHubService.getRepos(githubToken);
    }

    @PostMapping("/{owner}/{repo}/generate-readme")
    public String generate(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestBody ReadMeDTO req
    ) {

        String token = authHeader.replace("Bearer ", "");

        String githubToken = JWTService.extractGithubToken(token);

        String readme = gitHubService.getRepoReadme(
                githubToken,
                owner,
                repo
        );

        return generateService.generateReadme(
                repo,
                readme
        );
    }
}