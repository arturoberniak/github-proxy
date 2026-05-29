package com.example.github_proxy;

class GithubUserNotFoundException extends RuntimeException {

    GithubUserNotFoundException(String username) {
        super("GitHub user '%s' not found".formatted(username));
    }
}
