package com.nishtha.PrivateRepoReadMeGen.DTOs;

public class RepoDTO {
    private String name;
    private String fullName;
    private String description;
    private boolean isPrivate;

    public RepoDTO(String name, String fullName, String description, boolean isPrivate) {
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public String getDescription() { return description; }
    public boolean isPrivate() { return isPrivate; }
}
