# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-focal

# Copy the Aiven CA certificate
COPY src/main/resources/ca.pem /tmp/ca.pem

# Install the certificate into Java's main truststore
RUN keytool -importcert -alias aiven-ca -keystore ${JAVA_HOME}/lib/security/cacerts -file /tmp/ca.pem -storepass changeit -noprompt

# Set the working directory
WORKDIR /app

# Copy the application JAR
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]