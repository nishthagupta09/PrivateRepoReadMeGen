package com.nishtha.PrivateRepoReadMeGen.DTOs;

public class RepoDTO {
    private String name;
    private String fullName;

    public RepoDTO(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String getName() { return name; }
    public String getFullName() { return fullName; }
}
