# PrivateRepoReadMeGen — Backend

A Spring Boot REST API that signs users in with GitHub, lists their repositories (including private ones), and generates a `README.md` for any selected repository using Google's Gemini model.

- **Live app:** https://repo-read-me-gen.vercel.app
- **Frontend repo:** [PrivateRepoReadMeGen_Frontend](https://github.com/nishthagupta09/PrivateRepoReadMeGen_Frontend)

## How It Works

1. The frontend sends the user to `/oauth2/authorization/github`, starting Spring Security's GitHub OAuth2 login (scopes: `repo`, `user`).
2. After GitHub redirects back, `OAuthSuccessHandler` issues a signed JWT (HS256, 24-hour expiry) for the user's GitHub login and redirects the browser to the frontend with `?token=<jwt>`.
3. The frontend sends that JWT as a `Bearer` token on every API call. The backend uses it to identify the user and to call GitHub on their behalf.
4. `GET /private-repo` fetches the user's repositories from the GitHub API and returns each repo's `name` and `fullName`.
5. `POST /private-repo/{owner}/{repo}/generate-readme` fetches the repository's top-level contents from GitHub, builds a prompt from the repo name and that listing, sends it to Gemini (`gemini-2.5-flash`), and returns the generated Markdown as plain text.

## Tech Stack

- **Java 17**, **Spring Boot 3.4.9**
- **Spring Web** for the REST layer
- **Spring Security** with the **OAuth2 Client** (GitHub login)
- **jjwt 0.11.5** for JWT creation and validation
- **RestTemplate** for calls to the GitHub REST API and the Gemini API
- **Google Gemini** (`gemini-2.5-flash`, `generateContent` endpoint)
- **Maven** (wrapper included) and a multi-stage **Docker** build

## Project Structure

```
src/main/java/com/nishtha/PrivateRepoReadMeGen/
├── PrivateRepoReadMeGenApplication.java   # Spring Boot entry point
├── SecurityConfig.java                    # Security filter chain, OAuth2 login, CORS (deployed frontend origin)
├── OAuthSuccessHandler.java               # After GitHub login: issues the JWT and redirects to the frontend
├── client/
│   └── GeminiClient.java                  # Calls Gemini's generateContent API and extracts the text response
├── controller/
│   ├── RepoController.java                # /private-repo: list repos, generate a README
│   └── UserController.java                # /api/user: current user from the JWT
├── DTOs/
│   ├── RepoDTO.java                       # name, fullName
│   └── ReadMeDTO.java                     # request body: snippet
└── service/
    ├── GitHubService.java                 # GitHub API: user repos, repo top-level contents
    ├── GenerateService.java               # Builds the README-generation prompt and calls GeminiClient
    └── JWTService.java                    # Token generation and claim extraction

src/main/resources/application.yml         # OAuth2, Gemini and server config
Dockerfile                                 # Multi-stage build (Maven build, JDK runtime)
```

## API Endpoints

All endpoints other than the OAuth routes expect an `Authorization: Bearer <jwt>` header.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/oauth2/authorization/github` | Starts the GitHub OAuth2 login |
| `GET` | `/login/oauth2/code/github` | OAuth2 callback that GitHub redirects to |
| `GET` | `/api/user` | Returns `{ "loggedIn": true, "login": "...", "name": "..." }` for the token's user |
| `GET` | `/private-repo` | Returns the user's repositories as `[{ "name": "...", "fullName": "owner/repo" }]` |
| `POST` | `/private-repo/{owner}/{repo}/generate-readme` | Returns a generated README as plain-text Markdown |

The generate endpoint requires a JSON body, for example `{ "snippet": "" }`. The `snippet` field is accepted but not currently used when building the prompt.

```bash
curl -X POST \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{"snippet": ""}' \
  https://privatereporeadmegen.onrender.com/private-repo/<owner>/<repo>/generate-readme
```

## Configuration

Set these environment variables (they are read in `application.yml`):

| Variable | Purpose |
|---|---|
| `GITHUB_CLIENT_ID` | Client ID of your GitHub OAuth App |
| `GITHUB_CLIENT_SECRET` | Client secret of your GitHub OAuth App |
| `GEMINI_API_KEY` | Google Gemini API key |
| `PORT` | Server port (defaults to `8080`) |

Your GitHub OAuth App's **Authorization callback URL** must match the `redirect-uri` in `application.yml`, which is currently `https://privatereporeadmegen.onrender.com/login/oauth2/code/github`.

## Running Locally

**Prerequisites:** Java 17+, a GitHub OAuth App, and a Gemini API key. Maven is not required; the `mvnw` wrapper is included.

```bash
git clone https://github.com/nishthagupta09/PrivateRepoReadMeGen.git
cd PrivateRepoReadMeGen

export GITHUB_CLIENT_ID=your_client_id
export GITHUB_CLIENT_SECRET=your_client_secret
export GEMINI_API_KEY=your_gemini_key

./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

> **Note:** Several URLs are hard-coded to the deployed apps: the OAuth `redirect-uri` in `application.yml`, the post-login redirect in `OAuthSuccessHandler`, and the allowed CORS origin in `SecurityConfig` and the controllers' `@CrossOrigin`. To run the full login flow locally, change them to your local backend and frontend URLs.

### With Docker

```bash
docker build -t private-repo-readme-gen .

docker run -p 8080:8080 \
  -e GITHUB_CLIENT_ID=your_client_id \
  -e GITHUB_CLIENT_SECRET=your_client_secret \
  -e GEMINI_API_KEY=your_gemini_key \
  private-repo-readme-gen
```

The image builds with Maven 3.9.6 on Temurin JDK 21 and runs the packaged jar on `eclipse-temurin:21-jdk`.

### Tests

```bash
./mvnw test
```

The project currently has a single Spring context-load test, which needs the environment variables above to be set.

## Known Limitations

- **Top-level listing only.** The generator sends Gemini the repository name plus the names and types of entries in the repo's root directory. It does not read file contents or subdirectories, so the generated README is inferred and should be reviewed and edited.
- **Repository list is not paginated.** `GET /private-repo` returns what GitHub's `/user/repos` gives by default, so users with many repositories may not see all of them.
- **Errors are returned as text.** If generation fails, the endpoint responds with a plain-text message beginning `ERROR:` rather than an error status code.
- **`snippet` is unused.** The request DTO includes it, but the prompt is built only from the repo name and structure.

## Future Improvements

- Read the full repository tree and key files (build files, entry points, existing docs) so generated READMEs reflect the actual code
- Proper HTTP error responses and a global exception handler
- Paginated repository listing
- Use the `snippet` field to let users add their own context to the prompt
- Additional tests covering the controllers and services
