package com.nishtha.PrivateRepoReadMeGen.service;

import com.nishtha.PrivateRepoReadMeGen.DTOs.RepoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final RestTemplate rest = new RestTemplate();

    public List<RepoDTO> getRepos(String githubToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response =
                rest.exchange(
                        "https://api.github.com/user/repos",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().stream().map(repo ->
                new RepoDTO(
                        (String) repo.get("name"),
                        (String) repo.get("full_name")
                )
        ).toList();
    }

    public String getRepoReadme(String githubToken, String owner, String repo) {

        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/readme";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> res = rest.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            String encoded = (String) res.getBody().get("content");
            return new String(Base64.getDecoder().decode(encoded));

        } catch (Exception e) {
            e.printStackTrace();
            return "No existing README found. Generate a completely new README for this repository.";
        }
    }
}