package com.example.github_proxy;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GithubService {

    private final GithubClient githubClient;

    GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<Repository> listNonForkRepositories(String username) {
        return githubClient.fetchRepositories(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> new Repository(
                        repo.name(),
                        repo.owner().login(),
                        fetchBranches(username, repo.name())
                ))
                .toList();
    }

    private List<Branch> fetchBranches(String username, String repoName) {
        return githubClient.fetchBranches(username, repoName).stream()
                .map(b -> new Branch(b.name(), b.commit().sha()))
                .toList();
    }
}
