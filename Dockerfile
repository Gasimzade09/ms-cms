# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/ms-cms.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","ms-cms.jar"]
