####
# Dockerfile for Connecting Worlds using direct Maven approach
####

# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy POM file and source code
COPY pom.xml .
COPY src src

# Build the application directly with Maven
RUN mvn package -DskipTests

# Inspect what's in the target directory
RUN ls -la /app/target/

# Runtime stage
FROM amazoncorretto:21-alpine-jdk
WORKDIR /work

# Install required packages
RUN apk add --no-cache bash

# Create nonroot user/group
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus

# Set up volume for configuration
RUN mkdir -p /work/config && chown -R quarkus:quarkus /work

# Copy everything from target directory for debugging
COPY --from=build --chown=quarkus:quarkus /app/target/ /work/target/

# Copy Quarkus app folder (contains all dependencies and launcher scripts)
COPY --from=build --chown=quarkus:quarkus /app/target/quarkus-app/ /work/quarkus-app/

# Set environment variables
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV QUARKUS_CONFIG_LOCATIONS="/work/config"

# Set the user
USER quarkus

# Expose the application port
EXPOSE 8080

# Run the application with quarkus-run.jar (standard name for Quarkus applications)
ENTRYPOINT ["java", "-jar", "/work/quarkus-app/quarkus-run.jar"]