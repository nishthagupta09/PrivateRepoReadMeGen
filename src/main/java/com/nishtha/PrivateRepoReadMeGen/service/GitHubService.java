package com.nishtha.PrivateRepoReadMeGen.service;

import com.nishtha.PrivateRepoReadMeGen.DTOs.RepoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public String getRepoStructure(String githubToken, String owner, String repo) {

        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List> res = rest.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            StringBuilder structure = new StringBuilder();

            List<Map<String, Object>> files = res.getBody();

            for (Map<String, Object> file : files) {

                structure.append(file.get("type"))
                        .append(": ")
                        .append(file.get("name"))
                        .append("\n");
            }
            return structure.toString();
    }
}