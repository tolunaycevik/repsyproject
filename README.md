# Repsy Package Repository

A **minimal**, **developer-friendly** REST API for managing Repsy `.rep` packages and their metadata.

> _"This is my take on a simple package registry—no frills, just the essentials you need to deploy and fetch `.rep` archives in a reproducible way."_

---

## 🛠️ Features

- **Deploy**: Upload a `.rep` file (ZIP) and a `meta.json` to `/{name}/{version}`.
- **Download**: Fetch `.rep` or `meta.json` via `/{name}/{version}/{fileName}`.
- **Storage Strategy**: Switch between filesystem or MinIO (S3-compatible) with a simple config flag.
- **PostgreSQL**: Tracks package metadata and prevents accidental overwrites.
- **Validation & Error Handling**: Rejects bad requests with clear JSON error messages.
- **Dockerized**: One `docker-compose up` brings up Postgres, MinIO, and the app.

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

---

---

## 📤 Publishing to Repsy Maven & Docker Repositories

If you have access to your company’s **Repsy** repositories, follow these steps to publish both your storage libraries and Docker image.

### Publishing Libraries to Repsy Maven Repository

1. **Add distributionManagement** to each library’s `pom.xml` (`storage-api`, `storage-file-system`, `storage-object-minio`):
   ```xml
   <distributionManagement>
     <repository>
       <id>repsy-releases</id>
       <name>Repsy Release Repository</name>
       <url>https://repo.repsy.io/repository/maven-releases/</url>
     </repository>
   </distributionManagement>
   ```

2. **Configure Maven credentials** in `~/.m2/settings.xml`:
   ```xml
   <servers>
     <server>
       <id>repsy-releases</id>
       <username>${env.REPSY_MAVEN_USER}</username>
       <password>${env.REPSY_MAVEN_PASSWORD}</password>
     </server>
   </servers>
   ```

3. **Deploy the libraries**:
   ```bash
   cd storage-api
   mvn clean deploy -DskipTests
   cd ../storage-file-system
   mvn clean deploy -DskipTests
   cd ../storage-object-minio
   mvn clean deploy -DskipTests
   ```

4. **Update the `repsy-server/pom.xml`** to reference the released versions (e.g. `0.0.1`) from your Repsy Maven repo.

### Publishing Docker Image to Repsy Docker Registry

1. **Build and tag** the Docker image:
   ```bash
   docker build -t yourdockeruser/repsy-server:1.0.0 .
   ```

2. **Tag for Repsy registry**:
   ```bash
   docker tag yourdockeruser/repsy-server:1.0.0 registry.repsy.io/repsy-server:1.0.0
   ```

3. **Push** to your Repsy Docker registry:
   ```bash
   docker push registry.repsy.io/repsy-server:1.0.0
   ```

Interviewers can then run:
```bash
docker pull registry.repsy.io/repsy-server:1.0.0
```  

---

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


