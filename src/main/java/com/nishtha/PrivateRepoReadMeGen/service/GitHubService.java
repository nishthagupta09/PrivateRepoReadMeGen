package com.nishtha.PrivateRepoReadMeGen.service;

import com.nishtha.PrivateRepoReadMeGen.DTOs.RepoDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<RepoDTO> listRepos(OAuth2AuthorizedClient client) {
        String url = "https://api.github.com/user/repos?per_page=100";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(client.getAccessToken().getTokenValue());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);

        List<Map<String, Object>> rawRepos = response.getBody();

        return rawRepos.stream()
                .map(repo -> new RepoDTO(
                        (String) repo.get("name"),
                        (String) repo.get("full_name"),
                        (String) repo.get("description"),
                        (Boolean) repo.get("private")
                ))
                .collect(Collectors.toList());
    }

    public String getRepoReadme(OAuth2AuthorizedClient client, String username, String repo) {
        String url = "https://api.github.com/repos/" + username + "/" + repo + "/readme";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(client.getAccessToken().getTokenValue());
        headers.set("Accept", "application/vnd.github.v3.raw");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response.getBody();
    }
}