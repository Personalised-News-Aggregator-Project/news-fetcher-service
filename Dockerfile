# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# --- THIS IS THE NEW, IMPORTANT STEP ---
# Copy the truststore from the source code to a known path in the final container
COPY src/main/resources/kafka.truststore.jks /etc/ssl/certs/kafka.truststore.jks

# Copy the .jar file created in the 'build' stage
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar

# The command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]