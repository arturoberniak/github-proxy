package com.example.github_proxy;

import com.fasterxml.jackson.annotation.JsonProperty;

record GithubBranchDto(
        String name,
        @JsonProperty("commit") CommitDto commit
) {
    record CommitDto(String sha) {}
}
