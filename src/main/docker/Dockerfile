## Multi-stage build for Spring Boot (Java 11)

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /workspace

# Pass GitHub Packages credentials via build args
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=${GITHUB_ACTOR}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

# Copy Maven settings (uses env vars) and project files
COPY .mvn/ .mvn/
COPY pom.xml ./
COPY src/ ./src/

# Build the application (skip tests because CI already runs them)
RUN mvn -B -s .mvn/settings.xml package -DskipTests \
    && cp target/*.jar /workspace/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]