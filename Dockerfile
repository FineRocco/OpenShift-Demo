# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy project descriptor and source files
COPY pom.xml .
COPY src ./src

# Build executable jar package
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user with OpenShift root group membership
RUN adduser -D -u 1001 -G root appuser

# Copy jar from builder stage
COPY --from=builder /build/target/*.jar /app/app.jar

# Ensure proper permissions for non-root execution
RUN chown -R 1001:0 /app && \
    chmod -R g=u /app

# Run as non-root user
USER 1001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
