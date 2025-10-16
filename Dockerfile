# Stage 1: Build the application
FROM gradle:jdk17-alpine AS build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle bootJar --no-daemon

# Stage 2: Create the final runtime image
FROM openjdk:17-jdk-slim-buster
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]