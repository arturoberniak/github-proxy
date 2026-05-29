# GitHub Proxy

Small Spring Boot service that proxies GitHub API v3 to list a user's non-fork repositories and their branches.

## Requirements

- Java 25
- Gradle
- WireMock (for integration tests)

## API

- `GET /api/users/{username}/repositories`
  - Returns repository name, owner login, and branches with last commit SHA.
  - 404 response for a non-existing user:
    ```json
    {
      "status": 404,
      "message": "..."
    }
    ```

## Integration Testing

Integration tests start the application on a random port and use WireMock to emulate GitHub API responses, exercising the full Controller/Service/Client flow without mocks.

## Test Cases

- Test 1: Non-fork repositories
  - WireMock returns one non-fork and one forked repo; only the non-fork repo is returned.
  - Branches endpoint returns multiple branches; each branch includes the `name` and last commit `sha`.
- Test 2: All repositories are forks
  - WireMock returns only forked repositories; the API responds with an empty list and `200 OK`.
- Test 3: User not found
  - GitHub API responds with `404`; the API returns `404` with `{ "status": 404, "message": "...<username>..." }`.

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```
