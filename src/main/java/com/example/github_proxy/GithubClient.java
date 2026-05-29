package com.example.github_proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GithubClient {

    private static final ParameterizedTypeReference<List<GithubRepositoryDto>> REPO_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<GithubBranchDto>> BRANCH_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    GithubClient(@Value("${github.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    List<GithubRepositoryDto> fetchRepositories(String username) {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(REPO_LIST_TYPE);
    }

    List<GithubBranchDto> fetchBranches(String username, String repoName) {
        return restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .body(BRANCH_LIST_TYPE);
    }
}
