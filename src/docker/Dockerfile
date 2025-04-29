####
# Alternative Dockerfile that uses Maven directly without requiring the wrapper
####

# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Copy the entire project
COPY . .

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /work

# Install required packages
RUN apk add --no-cache bash

# Create nonroot user/group
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus

# Set up volume for configuration
RUN mkdir -p /work/config && chown -R quarkus:quarkus /work

# Copy JAR from build stage
COPY --from=build --chown=quarkus:quarkus /build/target/quarkus-app/ /work/
COPY --from=build --chown=quarkus:quarkus /build/target/*-runner.jar /work/application.jar

# Set environment variables
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV QUARKUS_CONFIG_LOCATIONS="/work/config"

# Set the user
USER quarkus

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/work/application.jar"]