# 🚀 OpenShift Demo — Spring Boot on Red Hat OpenShift

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenShift](https://img.shields.io/badge/Red%20Hat-OpenShift-red.svg)](https://www.redhat.com/en/technologies/cloud-computing/openshift)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-blue.svg)](https://www.docker.com/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD%20Pipeline-darkblue.svg)](https://www.jenkins.io/)
[![Ansible](https://img.shields.io/badge/Ansible-Automation-black.svg)](https://www.ansible.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Manifests-326CE5.svg)](https://kubernetes.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](#license)

A production-ready **Spring Boot 3.2.5** microservice designed to be deployed on **Red Hat OpenShift Container Platform** using **Jenkins CI/CD**, **Ansible automation**, and **Kubernetes manifests**. This project serves as a complete end-to-end learning template — from local development all the way to cloud deployment on OpenShift's free Developer Sandbox.

---

## 📑 Table of Contents

1. [About the Project](#about-the-project)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Prerequisites](#prerequisites)
6. [Getting Started — Local Development](#getting-started--local-development)
7. [Running Tests](#running-tests)
8. [Containerization with Docker](#containerization-with-docker)
9. [Deploying to OpenShift](#deploying-to-openshift)
   - [Method A: Manual Deployment with `oc` CLI](#method-a-manual-deployment-with-oc-cli)
   - [Method B: Automated Deployment with Ansible](#method-b-automated-deployment-with-ansible)
   - [Method C: CI/CD Pipeline with Jenkins](#method-c-cicd-pipeline-with-jenkins)
10. [API Endpoints Reference](#api-endpoints-reference)
11. [Kubernetes & OpenShift Manifests Explained](#kubernetes--openshift-manifests-explained)
12. [OpenShift Key Concepts](#openshift-key-concepts)
13. [Free Tier — Red Hat Developer Sandbox](#free-tier--red-hat-developer-sandbox)
14. [Troubleshooting](#troubleshooting)
15. [Contributing](#contributing)
16. [License](#license)

---

## About the Project

This project is a **simple, database-free Spring Boot REST API** built specifically to demonstrate how to:

- Build and containerize a Java application using a **multi-stage Docker build**
- Deploy it to **Red Hat OpenShift** (Kubernetes + enterprise features)
- Automate the CI/CD pipeline using **Jenkins** (Jenkinsfile)
- Automate infrastructure deployment using **Ansible** (playbooks)
- Use **Kubernetes-native manifests** (Deployments, Services, Routes, ConfigMaps)

The application exposes a few REST endpoints that return JSON responses — perfect for verifying that your deployment pipeline works end-to-end without worrying about database setup.

### What This Project Does NOT Use
- ❌ No database (H2, PostgreSQL, MySQL, etc.)
- ❌ No Spring Security / authentication
- ❌ No message queues or external services

This keeps things simple so you can focus on the **DevOps and deployment pipeline**.

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Java 17 (LTS) | Application runtime |
| **Framework** | Spring Boot 3.2.5 | REST API and application framework |
| **Build Tool** | Apache Maven 3.9+ | Dependency management and JAR packaging |
| **Health Monitoring** | Spring Boot Actuator | Health checks, metrics, and probes for Kubernetes |
| **Containerization** | Docker (multi-stage) | Lightweight, production-ready container images |
| **Container Platform** | Red Hat OpenShift 4.x | Enterprise Kubernetes platform |
| **CI/CD** | Jenkins (Declarative Pipeline) | Automated build, test, and deployment |
| **Automation** | Ansible 2.14+ | Infrastructure-as-code deployment orchestration |
| **Orchestration** | Kubernetes / OpenShift | Container scheduling, scaling, and routing |

---

## Architecture

```
                          ┌─────────────────────────────────────────────────────────────┐
                          │                  RED HAT OPENSHIFT CLUSTER                  │
                          │                                                             │
  ┌──────────┐   HTTPS    │  ┌──────────────┐    HTTP     ┌────────────────────┐        │
  │  Public   │──────────►│  │   OpenShift   │───────────►│   Kubernetes       │        │
  │  Client   │  (TLS     │  │   Route       │   :8080    │   Service          │        │
  │  Browser  │  Edge)    │  │   (edge TLS)  │            │   (ClusterIP)      │        │
  └──────────┘            │  └──────────────┘            └─────────┬──────────┘        │
                          │                                         │                   │
                          │                          ┌──────────────┼──────────────┐    │
                          │                          │              │              │    │
                          │                    ┌─────▼─────┐  ┌────▼──────┐       │    │
                          │                    │   Pod 1    │  │   Pod 2   │       │    │
                          │                    │ Spring Boot│  │Spring Boot│       │    │
                          │                    │  :8080     │  │  :8080    │       │    │
                          │                    │            │  │           │       │    │
                          │                    │ /actuator/ │  │/actuator/ │       │    │
                          │                    │  health ✓  │  │ health ✓  │       │    │
                          │                    └─────┬──────┘  └────┬──────┘       │    │
                          │                          │              │              │    │
                          │                    ┌─────▼──────────────▼──────┐       │    │
                          │                    │   ConfigMap (mounted)     │       │    │
                          │                    │ application-openshift     │       │    │
                          │                    │ .properties              │       │    │
                          │                    └──────────────────────────┘       │    │
                          │                                                       │    │
                          └───────────────────────────────────────────────────────────┘
```

### CI/CD Pipeline Flow

```
 ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────────┐     ┌───────────────────┐
 │ GitHub   │────►│ Jenkins  │────►│  Maven   │────►│ Docker Build │────►│ Deploy to         │
 │ Push     │     │ Checkout │     │ Build +  │     │ (Multi-stage)│     │ OpenShift         │
 │          │     │          │     │ Test     │     │              │     │ (oc start-build)  │
 └──────────┘     └──────────┘     └──────────┘     └──────────────┘     └───────────────────┘
```

---

## Project Structure

```
openshift-demo/
│
├── 📄 pom.xml                           # Maven project descriptor
│                                        #   → Spring Boot 3.2.5 parent
│                                        #   → Dependencies: web, actuator, test
│                                        #   → Java 17, spring-boot-maven-plugin
│
├── 🐳 Dockerfile                        # Multi-stage container build
│                                        #   → Stage 1: maven:3.9-eclipse-temurin-17 (build)
│                                        #   → Stage 2: eclipse-temurin:17-jre-alpine (run)
│                                        #   → Non-root user (UID 1001, GID 0)
│
├── 🔧 Jenkinsfile                       # Declarative CI/CD pipeline
│                                        #   → 5 stages: Checkout → Build → Test → Image → Deploy
│                                        #   → OpenShift credentials binding
│                                        #   → oc CLI commands for deployment
│
├── 📖 README.md                         # This file — full documentation
├── 🚫 .gitignore                        # Java/Maven/IDE ignore patterns
│
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/teejey/openshiftdemo/
│   │   │   ├── 📄 OpenShiftDemoApplication.java    # @SpringBootApplication entry point
│   │   │   └── 📂 controller/
│   │   │       └── 📄 HelloController.java         # REST API controller
│   │   │                                           #   → GET / (welcome + status)
│   │   │                                           #   → GET /api/hello (greeting + timestamp)
│   │   │                                           #   → GET /api/health (status + profile)
│   │   │                                           #   → GET /api/info (app metadata + java version)
│   │   └── 📂 resources/
│   │       └── 📄 application.properties           # Default config (port 8080, actuator)
│   └── 📂 test/
│       └── 📂 java/com/teejey/openshiftdemo/
│           ├── 📄 OpenShiftDemoApplicationTests.java  # Spring context loads test
│           └── 📂 controller/
│               └── 📄 HelloControllerTest.java     # @WebMvcTest with MockMvc
│                                                   #   → Tests all 4 endpoints
│
├── 📂 k8s/                             # Kubernetes & OpenShift manifests
│   ├── 📄 deployment.yml               # Deployment (2 replicas, probes, resource limits)
│   ├── 📄 service.yml                  # ClusterIP Service (port 8080)
│   ├── 📄 route.yml                    # OpenShift Route (TLS edge termination)
│   ├── 📄 configmap.yml                # ConfigMap (openshift Spring profile properties)
│   ├── 📄 buildconfig.yml              # OpenShift BuildConfig (binary Docker build)
│   └── 📄 imagestream.yml              # OpenShift ImageStream (image tag tracking)
│
└── 📂 ansible/                          # Ansible deployment automation
    ├── 📄 deploy-playbook.yml           # Main playbook (login → create project → apply → rollout)
    ├── 📄 inventory.ini                 # Localhost inventory
    └── 📄 vars.yml                      # Default variables (server, token, project, sizing)
```

---

## Prerequisites

Before you begin, make sure the following tools are installed:

### For Local Development
| Tool | Version | Installation |
|------|---------|-------------|
| **Java JDK** | 17 (LTS) | [Download](https://adoptium.net/temurin/releases/?version=17) |
| **Apache Maven** | 3.9+ | [Download](https://maven.apache.org/download.cgi) |

### For Containerization
| Tool | Version | Installation |
|------|---------|-------------|
| **Docker** | 24+ | [Download](https://docs.docker.com/get-docker/) |
| *OR* **Podman** | 4.4+ | [Download](https://podman.io/docs/installation) |

### For OpenShift Deployment
| Tool | Version | Installation |
|------|---------|-------------|
| **OpenShift CLI (`oc`)** | 4.12+ | [Download](https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/) |
| **Ansible** *(optional)* | 2.14+ | `pip install ansible` |
| **Jenkins** *(optional)* | 2.400+ | [Download](https://www.jenkins.io/download/) |

### Verify Installation

```bash
java -version          # Should show: openjdk version "17.x.x"
mvn -version           # Should show: Apache Maven 3.9.x
docker --version       # Should show: Docker version 24.x.x
oc version             # Should show: Client Version: 4.x.x
ansible --version      # Should show: ansible [core 2.14+]
```

---

## Getting Started — Local Development

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/openshift-demo.git
cd openshift-demo
```

### 2. Build the Project

```bash
mvn clean package
```

This compiles the code, runs tests, and generates `target/openshift-demo-1.0.0.jar`.

### 3. Run the Application

**Option A — Using Maven (hot-reload for development):**
```bash
mvn spring-boot:run
```

**Option B — Using the compiled JAR:**
```bash
java -jar target/openshift-demo-1.0.0.jar
```

**Option C — With a specific profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=openshift
```

### 4. Verify It's Working

Open your browser or use `curl`:

```bash
# Welcome endpoint
curl http://localhost:8080/
# → {"message":"Welcome to OpenShift Demo!","status":"running","version":"1.0.0"}

# Hello endpoint
curl http://localhost:8080/api/hello
# → {"greeting":"Hello from OpenShift!","timestamp":"2026-09-28T20:45:00.123Z"}

# Health check
curl http://localhost:8080/api/health
# → {"status":"UP","environment":"default"}

# App info
curl http://localhost:8080/api/info
# → {"app":"openshift-demo","description":"A Spring Boot app deployed on OpenShift","java":"17.0.x"}

# Spring Actuator health (used by Kubernetes probes)
curl http://localhost:8080/actuator/health
# → {"status":"UP","components":{"diskSpace":{...},"ping":{...}}}
```

---

## Running Tests

The project includes two test classes:

| Test Class | Type | What It Tests |
|-----------|------|---------------|
| `OpenShiftDemoApplicationTests` | `@SpringBootTest` | Spring context loads successfully |
| `HelloControllerTest` | `@WebMvcTest` | All 4 REST endpoints return correct JSON |

### Run All Tests

```bash
mvn test
```

### Run a Specific Test Class

```bash
mvn test -Dtest=HelloControllerTest
```

### View Test Reports

After running tests, surefire reports are generated at:
```
target/surefire-reports/
```

---

## Containerization with Docker

The project uses a **multi-stage Dockerfile** for optimal image size and security.

### How the Dockerfile Works

| Stage | Base Image | Purpose |
|-------|-----------|---------|
| **Stage 1 (Builder)** | `maven:3.9-eclipse-temurin-17` | Compiles source code and packages the JAR (~500MB, discarded after build) |
| **Stage 2 (Runtime)** | `eclipse-temurin:17-jre-alpine` | Runs the JAR in a minimal Alpine image (~80MB final image) |

**Security features:**
- Runs as **non-root user** (UID `1001`)
- User belongs to **root group** (GID `0`) — required by OpenShift's [Security Context Constraints](https://docs.openshift.com/container-platform/latest/authentication/managing-security-context-constraints.html)
- File permissions set with `chmod -R g=u` so OpenShift's arbitrary UID can read/write

### Build the Docker Image

```bash
docker build -t openshift-demo:latest .
```

### Run the Container Locally

```bash
docker run -d \
  --name openshift-demo \
  -p 8080:8080 \
  openshift-demo:latest
```

### Verify the Container

```bash
# Check it's running
docker ps

# Test endpoints
curl http://localhost:8080/
curl http://localhost:8080/actuator/health

# View logs
docker logs openshift-demo

# Stop and remove
docker stop openshift-demo && docker rm openshift-demo
```

---

## Deploying to OpenShift

This project supports **three deployment methods**. Choose the one that fits your workflow.

> **📌 Before you begin:** You need an OpenShift cluster. The easiest free option is the [Red Hat Developer Sandbox](#free-tier--red-hat-developer-sandbox) — see instructions below.

---

### Method A: Manual Deployment with `oc` CLI

Best for: **Learning and understanding** each step of the deployment process.

#### Step 1 — Login to OpenShift

```bash
oc login --server=https://<your-openshift-api-url>:6443 --token=<your-token>
```

> 💡 Get your token from the OpenShift Web Console: click your username → **Copy login command** → **Display Token**

#### Step 2 — Create a Project (Namespace)

```bash
oc new-project openshift-demo
```

#### Step 3 — Apply All Kubernetes Manifests

This applies the ConfigMap, ImageStream, BuildConfig, Deployment, Service, and Route:

```bash
oc apply -f k8s/
```

**Expected output:**
```
configmap/openshift-demo-config created
deployment.apps/openshift-demo created
service/openshift-demo created
route.route.openshift.io/openshift-demo created
buildconfig.build.openshift.io/openshift-demo created
imagestream.image.openshift.io/openshift-demo created
```

#### Step 4 — Build the Container Image Inside OpenShift

Upload your source code to OpenShift and trigger a build:

```bash
oc start-build openshift-demo --from-dir=. --follow
```

This sends your project files to OpenShift, which runs the Dockerfile inside the cluster and pushes the resulting image to the internal registry.

#### Step 5 — Verify Deployment

```bash
# Check rollout status
oc rollout status deployment/openshift-demo

# Check pods are running
oc get pods -l app=openshift-demo

# Get the public URL
oc get route openshift-demo
```

#### Step 6 — Access Your Application

```bash
# Get the route hostname
ROUTE=$(oc get route openshift-demo -o jsonpath='{.spec.host}')

# Test it
curl https://$ROUTE/
curl https://$ROUTE/api/hello
curl https://$ROUTE/actuator/health
```

#### Cleanup

```bash
oc delete project openshift-demo
```

---

### Method B: Automated Deployment with Ansible

Best for: **Repeatable, automated deployments** with a single command.

The Ansible playbook automates all the manual `oc` steps above into a single command.

#### What the Playbook Does

1. ✅ Authenticates to OpenShift cluster (`oc login`)
2. ✅ Checks if the project exists; creates it if missing (`oc new-project`)
3. ✅ Applies all K8s/OpenShift manifests (`oc apply -f k8s/`)
4. ✅ Waits for deployment rollout to complete (up to 180s timeout)
5. ✅ Retrieves and displays the public Route URL

#### Configure Variables

Edit `ansible/vars.yml` with your cluster details:

```yaml
app_name: "openshift-demo"
openshift_project: "openshift-demo"
openshift_server: "https://api.sandbox.x8i4.p1.openshiftapps.com:6443"
openshift_token: ""              # Your oc login token
image_tag: "latest"
k8s_dir: "../k8s"
```

#### Run the Playbook

```bash
# Option 1: Edit vars.yml first, then run
ansible-playbook -i ansible/inventory.ini ansible/deploy-playbook.yml

# Option 2: Override variables via command line
ansible-playbook -i ansible/inventory.ini ansible/deploy-playbook.yml \
  -e "openshift_server=https://api.sandbox.x8i4.p1.openshiftapps.com:6443" \
  -e "openshift_token=sha256~YOUR_TOKEN_HERE" \
  -e "openshift_project=openshift-demo"
```

#### Expected Output

```
TASK [5. Display route URL and deployment summary] ****************************
ok: [localhost] => {
    "msg": [
        "=================================================================",
        " Red Hat OpenShift Deployment Completed Successfully!",
        "=================================================================",
        " Application Name : openshift-demo",
        " Target Project   : openshift-demo",
        " Route URL        : https://openshift-demo-openshift-demo.apps.sandbox.x8i4.p1.openshiftapps.com",
        " Health Endpoint  : https://openshift-demo-openshift-demo.apps.sandbox.../actuator/health",
        " REST Greeting    : https://openshift-demo-openshift-demo.apps.sandbox.../api/hello",
        " Info Endpoint    : https://openshift-demo-openshift-demo.apps.sandbox.../api/info",
        "================================================================="
    ]
}
```

---

### Method C: CI/CD Pipeline with Jenkins

Best for: **Automated build, test, and deploy** triggered by every GitHub push.

#### Pipeline Stages

```
┌────────────┐   ┌────────────┐   ┌────────────┐   ┌────────────────┐   ┌──────────────────┐
│  1. Check  │──►│  2. Build  │──►│  3. Test   │──►│  4. Build      │──►│  5. Deploy to    │
│     out    │   │   (Maven)  │   │  (JUnit)   │   │     Image      │   │     OpenShift    │
│            │   │            │   │            │   │   (Docker)     │   │   (oc CLI)       │
└────────────┘   └────────────┘   └────────────┘   └────────────────┘   └──────────────────┘
```

#### Jenkins Setup Steps

**1. Install Required Plugins:**
- Pipeline Plugin
- Git Plugin
- Maven Integration Plugin
- Docker Pipeline Plugin
- Credentials Plugin

**2. Configure Global Tools** (Manage Jenkins → Tools):
- Add Maven installation named **`Maven-3.9`**
- Add JDK installation named **`JDK-17`**

**3. Add Credentials** (Manage Jenkins → Credentials → System → Global):

| Credential ID | Type | Value |
|--------------|------|-------|
| `openshift-server` | Secret text | `https://api.sandbox.x8i4.p1.openshiftapps.com:6443` |
| `openshift-token` | Secret text | Your `oc login` token (e.g., `sha256~xxxxx`) |

**4. Ensure `oc` CLI** is installed on the Jenkins agent and available in `PATH`.

**5. Create a Pipeline Job:**
1. New Item → Pipeline
2. Under Pipeline, select **"Pipeline script from SCM"**
3. Set SCM to **Git**, enter your GitHub repo URL
4. Branch: `*/main`
5. Script Path: `Jenkinsfile`
6. Save and **Build Now**

#### What the Jenkinsfile Does

The Jenkinsfile at the project root defines 5 stages:

| Stage | What It Does |
|-------|-------------|
| **Checkout** | Clones the repo from SCM |
| **Build** | Runs `mvn clean package -DskipTests` to compile and package |
| **Test** | Runs `mvn test` and archives JUnit test reports |
| **Build Image** | Runs `docker build -t openshift-demo:latest .` |
| **Deploy to OpenShift** | Authenticates, creates project, triggers `oc start-build`, deploys the app, and exposes the route |

**Post-build actions:**
- ✅ **Success**: Prints deployment success banner
- ❌ **Failure**: Prints failure message with instructions
- 🧹 **Always**: Cleans up the Jenkins workspace

---

## API Endpoints Reference

### Application Endpoints

| Method | Endpoint | Description | Example Response |
|--------|----------|-------------|-----------------|
| `GET` | `/` | Welcome message with app status | `{"message":"Welcome to OpenShift Demo!","status":"running","version":"1.0.0"}` |
| `GET` | `/api/hello` | Greeting with current UTC timestamp | `{"greeting":"Hello from OpenShift!","timestamp":"2026-09-28T20:45:00.123Z"}` |
| `GET` | `/api/health` | App health status + active Spring profile | `{"status":"UP","environment":"openshift"}` |
| `GET` | `/api/info` | Application metadata + Java runtime version | `{"app":"openshift-demo","description":"A Spring Boot app deployed on OpenShift","java":"17.0.12"}` |

### Spring Actuator Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Kubernetes liveness/readiness probe endpoint (used by OpenShift) |
| `GET` | `/actuator/info` | Application info (Spring Boot Actuator) |
| `GET` | `/actuator/metrics` | JVM and system metrics (memory, CPU, threads, HTTP stats) |

> 💡 The `/actuator/health` endpoint is what OpenShift uses for **liveness** and **readiness probes** to know if your app is healthy and ready to receive traffic.

---

## Kubernetes & OpenShift Manifests Explained

All manifests are in the `k8s/` directory. Here's what each one does:

### `deployment.yml` — Deployment

The core workload definition. Tells OpenShift how to run your application.

| Setting | Value | Why |
|---------|-------|-----|
| **Replicas** | 2 | High availability — if one pod dies, the other keeps serving |
| **Strategy** | RollingUpdate (maxSurge: 1, maxUnavailable: 0) | Zero-downtime deployments — new pods start before old ones stop |
| **Image** | `image-registry.openshift-image-registry.svc:5000/openshift-demo/openshift-demo:latest` | Pulls from OpenShift's internal image registry |
| **CPU Request/Limit** | 250m / 500m | Guarantees 0.25 CPU cores, can burst to 0.5 |
| **Memory Request/Limit** | 128Mi / 256Mi | Guarantees 128MB RAM, can use up to 256MB |
| **Liveness Probe** | `GET /actuator/health` every 10s, initial delay 30s | Restarts the container if it becomes unhealthy |
| **Readiness Probe** | `GET /actuator/health` every 5s, initial delay 10s | Removes pod from Service if not ready (no traffic sent) |
| **Environment** | `SPRING_PROFILES_ACTIVE=openshift` | Activates the `openshift` Spring profile from the ConfigMap |

### `service.yml` — Service

A stable internal endpoint that load-balances traffic across your pods.

- **Type**: `ClusterIP` (internal only — not exposed outside the cluster directly)
- **Port**: 8080 → 8080 (Service port maps to container port)
- **Selector**: `app: openshift-demo` (routes to pods with this label)

### `route.yml` — Route (OpenShift-specific)

Exposes your Service to the outside world with a public URL. This is OpenShift's equivalent of a Kubernetes Ingress.

- **TLS Termination**: `edge` — TLS is terminated at the router, traffic inside the cluster is HTTP
- **Insecure Traffic**: `Redirect` — HTTP requests are automatically redirected to HTTPS

### `configmap.yml` — ConfigMap

Externalized configuration that gets mounted into the container as a file.

The ConfigMap contains `application-openshift.properties` which is mounted at `/app/config/` inside the container. When `SPRING_PROFILES_ACTIVE=openshift`, Spring Boot automatically picks up these properties.

**Includes:**
- Actuator endpoint exposure (health, info, metrics, prometheus)
- Kubernetes health probe flags (liveness + readiness state)
- Graceful shutdown (20s timeout for in-flight requests)
- Debug logging for `com.teejey.openshiftdemo` package

### `buildconfig.yml` — BuildConfig (OpenShift-specific)

Tells OpenShift **how to build** your container image.

- **Source Type**: `Binary` — source code is uploaded via `oc start-build --from-dir=.`
- **Strategy**: `Docker` — uses the Dockerfile in the project root
- **Output**: Pushes the built image to `ImageStreamTag openshift-demo:latest`
- **Triggers**: `ConfigChange` + `ImageChange` — auto-rebuilds when config or base images change

### `imagestream.yml` — ImageStream (OpenShift-specific)

An OpenShift resource that **tracks container image tags**. Think of it as a pointer/alias to images in the internal registry.

- **Lookup Policy**: `local: true` — allows pods in the same project to reference the image by its ImageStream name instead of the full registry URL

---

## OpenShift Key Concepts

If you're new to OpenShift, here's a quick primer on the concepts used in this project:

| Concept | What It Is | Kubernetes Equivalent |
|---------|-----------|----------------------|
| **Project** | A namespace with extra features (RBAC, quotas, self-service) | Namespace |
| **Route** | Exposes a Service externally with a hostname and TLS | Ingress |
| **BuildConfig** | Defines how to build container images inside the cluster | No direct equivalent (uses external CI) |
| **ImageStream** | Tracks and manages container image tags internally | No direct equivalent |
| **`oc` CLI** | OpenShift's CLI tool (superset of `kubectl`) | `kubectl` |
| **Security Context Constraints (SCC)** | Policies controlling what pods can do (run as root, etc.) | Pod Security Standards |
| **Internal Registry** | Built-in container image registry at `image-registry.openshift-image-registry.svc:5000` | No built-in equivalent |

### Why OpenShift and Not Plain Kubernetes?

OpenShift adds enterprise features on top of Kubernetes:
- ✅ Built-in **container image registry** (no need for Docker Hub)
- ✅ Built-in **CI/CD with BuildConfigs** (build images in-cluster)
- ✅ **Routes** for easy external access with TLS
- ✅ **Security Context Constraints** enforce non-root containers by default
- ✅ **Web Console** with developer and admin perspectives
- ✅ **Free Developer Sandbox** for learning

---

## Free Tier — Red Hat Developer Sandbox

You can deploy this project **completely free** using the **Developer Sandbox for Red Hat OpenShift**.

### What You Get (Free)

| Feature | Details |
|---------|---------|
| **Cost** | Free — no credit card required |
| **Duration** | 30 days per session (can re-register unlimited times) |
| **Cluster** | Fully managed, shared multi-tenant OpenShift 4.x cluster |
| **Projects** | 2 pre-created projects (namespaces) |
| **Tools** | Red Hat OpenShift Dev Spaces (browser-based IDE) |
| **Languages** | Java, Node.js, Python, Go, C#, and more |

### How to Get Started

#### 1. Create a Free Red Hat Account
Go to [**developers.redhat.com/developer-sandbox**](https://developers.redhat.com/developer-sandbox) and sign up.

#### 2. Verify Your Identity
You'll need to verify via a mobile phone number (one-time SMS verification).

#### 3. Launch Your Sandbox
Click **"Start using your sandbox"** to provision your environment (takes ~30 seconds).

#### 4. Get Your Login Credentials
1. Open the **OpenShift Web Console**
2. Click your **username** in the top-right corner
3. Click **"Copy login command"**
4. Click **"Display Token"**
5. You'll see something like:

```bash
oc login --token=sha256~XXXXXXXXXXXXXXXXXX --server=https://api.sandbox-m2.ll9k.p1.openshiftapps.com:6443
```

Copy this command and run it in your terminal.

#### 5. Deploy This Project

```bash
# Clone and enter the project
git clone https://github.com/<your-username>/openshift-demo.git
cd openshift-demo

# Login to OpenShift (paste the command from step 4)
oc login --token=sha256~YOUR_TOKEN --server=https://api.sandbox.xxxx.p1.openshiftapps.com:6443

# Apply manifests and build
oc apply -f k8s/
oc start-build openshift-demo --from-dir=. --follow

# Get your public URL
oc get route openshift-demo
```

> ⚠️ **Note:** The Developer Sandbox has resource quotas. The project's resource requests (128Mi RAM, 250m CPU) are designed to fit comfortably within these limits.

---

## Troubleshooting

### Build Fails

```bash
# Check build logs
oc logs -f bc/openshift-demo

# Check build status
oc get builds

# Restart a failed build
oc start-build openshift-demo --from-dir=. --follow
```

### Pods Not Starting

```bash
# Check pod status
oc get pods -l app=openshift-demo

# Check pod events (look for image pull errors, OOM, etc.)
oc describe pod <pod-name>

# Check pod logs
oc logs <pod-name>

# If pods are in CrashLoopBackOff, check the previous logs
oc logs <pod-name> --previous
```

### Health Probes Failing

```bash
# Test the health endpoint directly from inside the cluster
oc exec <pod-name> -- curl -s http://localhost:8080/actuator/health

# Check if the readiness probe is passing
oc get pods -l app=openshift-demo -o wide
```

### Route Not Working

```bash
# Check route exists
oc get routes

# Check the route details
oc describe route openshift-demo

# Check the service has endpoints
oc get endpoints openshift-demo
```

### Common Issues

| Symptom | Cause | Fix |
|---------|-------|-----|
| `ImagePullBackOff` | Image hasn't been built yet | Run `oc start-build openshift-demo --from-dir=. --follow` |
| `CrashLoopBackOff` | App crashing on startup | Check `oc logs <pod>` — likely a config issue |
| Readiness probe failing | App takes too long to start | Increase `initialDelaySeconds` in `deployment.yml` |
| `403 Forbidden` on route | TLS or authentication issue | Check route TLS config with `oc describe route` |
| Resource quota exceeded | Sandbox limits reached | Reduce replicas to 1 or lower resource limits |

---

## Contributing

1. **Fork** this repository
2. **Create** a feature branch: `git checkout -b feature/my-feature`
3. **Commit** your changes: `git commit -m "Add my feature"`
4. **Push** to the branch: `git push origin feature/my-feature`
5. **Open** a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  <b>Built with ❤️ for learning OpenShift, Jenkins, Ansible, and Kubernetes</b>
</p>
