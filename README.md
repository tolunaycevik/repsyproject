# Repsy Project Repository


---


## 📦 Tech Stack

- **Java 17** & **Spring Boot 3**
- **PostgreSQL 15** (via Docker)
- **MinIO** (S3-compatible object store)
- **Maven** for build lifecycle
- **Docker & Docker Compose** for easy setup
- **JUnit 5** & **Spring MockMvc** for testing

---

## 🔮 Quickstart

### Prerequisites

- Docker & Docker Compose installed
- (Optional) Maven 3.8+ if you prefer local builds
- **For Windows PowerShell users:** call the external `curl.exe` or use `Invoke-RestMethod`

### 1. Clone & Navigate

```bash
git clone <your-repo-url>
cd repsy-interview-ready
```

### 2. Bring up the Stack

```bash
docker-compose up --build -d
```

- **Postgres** listens on `localhost:5432`
- **MinIO** on `localhost:9000` (`minioadmin` / `minioadmin`)
- **Your App** on `localhost:8080`

Wait ~10s until you see `Tomcat started on port(s): 8080` in the logs:

```bash
docker-compose logs -f app
```


## 🚀 API Endpoints

| Endpoint                               | Method | Description               |
|----------------------------------------|--------|---------------------------|
| `/{name}/{version}`                    | POST   | Deploy `.rep` + `meta.json` |
| `/{name}/{version}/package.rep`        | GET    | Download the binary package |
| `/{name}/{version}/meta.json`          | GET    | Download the metadata JSON  |

---

### Example: Deploy & Fetch

```bash
# Linux/macOS
curl -v -X POST http://localhost:8080/mypackage/1.0.0 \
  -F "package.rep=@test-files/package.rep" \
  -F "meta.json=@test-files/meta.json"

# Windows PowerShell
& curl.exe -v -X POST "http://localhost:8080/mypackage/1.0.0" `
  -F "package.rep=@test-files\package.rep" `
  -F "meta.json=@test-files\meta.json"

# Or using Invoke-RestMethod in PowerShell:
Invoke-RestMethod -Uri "http://localhost:8080/mypackage/1.0.0" -Method Post -Form @{
  "package.rep" = Get-Item "test-files\package.rep"
  "meta.json"  = Get-Item "test-files\meta.json"
}
```

```bash
# Linux/macOS download
curl -v http://localhost:8080/mypackage/1.0.0/package.rep --output out.rep
curl -v http://localhost:8080/mypackage/1.0.0/meta.json --output out-meta.json

# Windows PowerShell
& curl.exe -v "http://localhost:8080/mypackage/1.0.0/package.rep" --output out.rep
& curl.exe -v "http://localhost:8080/mypackage/1.0.0/meta.json" --output out-meta.json

# Or using Invoke-RestMethod:
Invoke-RestMethod -Uri "http://localhost:8080/mypackage/1.0.0/package.rep" -OutFile out.rep
Invoke-RestMethod -Uri "http://localhost:8080/mypackage/1.0.0/meta.json" -OutFile out-meta.json
```

---



### Manual Invalid‐Input Checks

```bash
# Linux/macOS: Missing meta.json → 400
curl -v -X POST http://localhost:8080/foo/1.0.1 -F "package.rep=@test-files/package.rep"

# Windows PowerShell:
& curl.exe -v -X POST "http://localhost:8080/foo/1.0.1" `
  -F "package.rep=@test-files\package.rep"

# Or Invoke-RestMethod:
Invoke-RestMethod -Uri "http://localhost:8080/foo/1.0.1" -Method Post -Form @{
  "package.rep" = Get-Item "test-files\package.rep"
}

# Linux/macOS: Missing package.rep → 400
curl -v -X POST http://localhost:8080/foo/1.0.1 -F "meta.json=@test-files/meta.json"

# Windows PowerShell:
& curl.exe -v -X POST "http://localhost:8080/foo/1.0.1" `
  -F "meta.json=@test-files\meta.json"

# Extra field → 400
curl -v -X POST http://localhost:8080/foo/1.0.1 \
  -F "package.rep=@test-files/package.rep" \
  -F "meta.json=@test-files/meta.json" \
  -F "oops=@test-files/package.rep"

# Windows PowerShell:
& curl.exe -v -X POST "http://localhost:8080/foo/1.0.1" `
  -F "package.rep=@test-files\package.rep" `
  -F "meta.json=@test-files\meta.json" `
  -F "oops=@test-files\package.rep"

# Malformed JSON → 400
echo '{ broken' > bad.json
curl -v -X POST http://localhost:8080/foo/1.0.1 \
  -F "package.rep=@test-files/package.rep" \
  -F "meta.json=@bad.json"

# Windows PowerShell:
"{ broken" | Out-File bad.json
& curl.exe -v -X POST "http://localhost:8080/foo/1.0.1" `
  -F "package.rep=@test-files\package.rep" `
  -F "meta.json=@bad.json"
```

---


