# Stage 1: Build the jar
FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar build/service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/service.jar"]
