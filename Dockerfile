# --- Stage 1: The Build Environment ---
# This stage uses a Docker image that has Maven and Java 17, just like your local setup.
# It's a temporary "workshop" to build your application.
FROM maven:3.9.9-eclipse-temurin-17-focal AS build

# Set the working directory inside the container to /app
WORKDIR /app

# Copy the project's dependency list first. This is a Docker optimization.
# If your code changes but your dependencies don't, Docker can reuse this layer.
COPY pom.xml .

# Download all the dependencies defined in pom.xml
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Build the application using Maven. This compiles your code and creates the .jar file.
# We skip the tests to make the deployment build faster.
RUN mvn clean package -DskipTests


# --- Stage 2: Create the final, lightweight image ---
# Use the official lightweight Java image
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# --- THIS IS THE CORRECTED LINE ---
# We are now using the exact filename from the build logs instead of a wildcard.
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar

# This command tells Render how to start your application.
ENTRYPOINT ["java", "-jar", "/app.jar"]