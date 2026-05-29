package com.example.github_proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(
        @ConfigureWireMock(name = "github-api", baseUrlProperties = "github.api.base-url")
)
class GithubControllerIntegrationTest {

    @InjectWireMock("github-api")
    private WireMockServer wireMock;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @org.junit.jupiter.api.BeforeEach
    void setUpClient() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturnNonForkRepositoriesWithBranches() {
        wireMock.stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "Hello-World",
                            "fork": false,
                            "owner": { "login": "octocat" }
                          },
                          {
                            "name": "forked-repo",
                            "fork": true,
                            "owner": { "login": "octocat" }
                          }
                        ]
                        """)));

        wireMock.stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "main",
                            "commit": { "sha": "abc123" }
                          },
                          {
                            "name": "feature",
                            "commit": { "sha": "def456" }
                          }
                        ]
                        """)));

        ResponseEntity<Repository[]> response =
                restClient.get()
                        .uri("/api/users/octocat/repositories")
                        .retrieve()
                        .toEntity(Repository[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Repository[] repos = response.getBody();
        assertThat(repos).hasSize(1);

        Repository repo = repos[0];
        assertThat(repo.name()).isEqualTo("Hello-World");
        assertThat(repo.ownerLogin()).isEqualTo("octocat");
        assertThat(repo.branches()).hasSize(2);
        assertThat(repo.branches().get(0).name()).isEqualTo("main");
        assertThat(repo.branches().get(0).lastCommitSha()).isEqualTo("abc123");
        assertThat(repo.branches().get(1).name()).isEqualTo("feature");
        assertThat(repo.branches().get(1).lastCommitSha()).isEqualTo("def456");
    }

    @Test
    void shouldReturnEmptyListWhenAllRepositoriesAreForks() {
        wireMock.stubFor(get(urlEqualTo("/users/forkmaster/repos"))
                .willReturn(okJson("""
                        [
                          {
                            "name": "some-fork",
                            "fork": true,
                            "owner": { "login": "forkmaster" }
                          }
                        ]
                        """)));

        ResponseEntity<Repository[]> response =
                restClient.get()
                        .uri("/api/users/forkmaster/repositories")
                        .retrieve()
                        .toEntity(Repository[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturn404WithErrorBodyForNonExistingUser() {
        wireMock.stubFor(get(urlEqualTo("/users/ghost-user/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                { "message": "Not Found" }
                                """)));

        ResponseEntity<ErrorResponseDto> response =
                restClient.get()
                        .uri("/api/users/ghost-user/repositories")
                        .retrieve()
                        .onStatus(status -> status.value() == 404, (request, responseBody) -> {
                        })
                        .toEntity(ErrorResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.message()).contains("ghost-user");
    }
}
