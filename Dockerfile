# --- Stage 1: The Build Environment ---
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: The Final Runtime Environment ---
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# --- NEW LINES ADDED HERE ---
# Create a directory for the certificate and copy it in
RUN mkdir -p /opt/aiven
COPY ca.pem /opt/aiven/

# Use Java's 'keytool' to import the Aiven certificate into the default truststore.
# The default password for the Java truststore is 'changeit'.
RUN keytool -importcert -alias aiven-ca -keystore $JAVA_HOME/lib/security/cacerts \
    -file /opt/aiven/ca.pem -storepass changeit -noprompt

# --- END OF NEW LINES ---

# Copy the .jar file from the 'build' stage
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]