package com.example.github_proxy;

import com.fasterxml.jackson.annotation.JsonProperty;

record GithubRepositoryDto(
        String name,
        boolean fork,
        @JsonProperty("owner") OwnerDto owner
) {
    record OwnerDto(
            String login
    ) {}
}
