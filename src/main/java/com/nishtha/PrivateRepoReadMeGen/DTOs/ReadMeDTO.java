package com.nishtha.PrivateRepoReadMeGen.DTOs;

public class ReadMeDTO {
    private String repoName;
    private String generatedReadme;

    public ReadMeDTO(String repoName, String generatedReadme) {
        this.repoName = repoName;
        this.generatedReadme = generatedReadme;
    }

    public String getRepoName() { return repoName; }
    public String getGeneratedReadme() { return generatedReadme; }
}
