# OpenShift Application Delivery Pipeline

A modern, cloud-native DevSecOps project demonstrating how to architect, containerize, and deploy a Spring Boot application to Red Hat OpenShift. This project features a fully automated event-driven CI/CD pipeline using Jenkins and Ansible, complete with centralized observability via the ELK Stack.

---

## 🏗️ Architecture & Technologies

*   **Application:** Java 17, Spring Boot 3.2.x (REST API)
*   **Platform:** Red Hat OpenShift (Kubernetes)
*   **CI/CD Pipeline:** Jenkins (Declarative Pipeline with GitHub Webhooks)
*   **Infrastructure as Code:** Ansible Execution Environments
*   **Observability:** ELK Stack (Elasticsearch, Logstash, Kibana)
*   **Artifacts:** OpenShift Internal Image Registry, Multi-Stage Docker Builds

## 🚀 Pipeline Flow
1. **Code Push:** Developer pushes to GitHub.
2. **Webhook Trigger:** GitHub instantly notifies the OpenShift Jenkins server.
3. **Automated Deploy:** Jenkins spawns ephemeral containers, builds the code, and delegates the Kubernetes rollout to an Ansible playbook.
4. **Centralized Logging:** The running Spring Boot pods automatically stream real-time JSON logs over TCP to Logstash, which aggregates them in Elasticsearch for Kibana visualization.

---

## 📚 API Endpoints

The application exposes the following endpoints (available over HTTPS via OpenShift Routes):

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | `GET` | Root welcome message |
| `/api/hello` | `GET` | Returns a greeting and current ISO timestamp |
| `/api/health` | `GET` | Service status (UP/DOWN) and active Spring profile |
| `/api/info` | `GET` | Application metadata and runtime Java version |

---

## 🛠️ Deployment Instructions

To deploy this entire infrastructure into an OpenShift cluster:

### 1. Deploy the ELK Stack
```bash
oc apply -f k8s/elk/
```

### 2. Deploy the Application & Pipeline
```bash
# Apply Kubernetes resources (Deployment, Service, Route)
oc apply -f k8s/

# Build the Java application and push to internal registry
oc start-build openshift-demo --from-dir=. --follow
```

### 3. Force Image Update (If Updating Code)
Because the deployment uses `imagePullPolicy: Always`, you can force the pods to pull the latest image build by triggering a rollout:
```bash
oc set env deployment/openshift-demo RESTART_ME=1
```

---

## 📊 Viewing Logs in Kibana

1. Get your public Kibana URL:
   ```bash
   oc get route kibana
   ```
2. Open the URL in your browser.
3. Go to **Stack Management** > **Index Patterns** > **Create index pattern**.
4. Type `springboot-logs-*` and select `@timestamp` as the time field.
5. Navigate to **Discover** to view and filter your real-time cluster logs!
