# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-focal

# Create the directory for the secrets
RUN mkdir -p /etc/secrets

# Copy the keystore and truststore directly from the build context
COPY src/main/resources/client.keystore.p12 /etc/secrets/client.keystore.p12
COPY src/main/resources/client.truststore.jks /etc/secrets/client.truststore.jks

WORKDIR /app

# Copy the application JAR
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]