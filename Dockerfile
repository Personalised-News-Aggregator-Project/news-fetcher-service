# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy the JAR file and the new entrypoint script
COPY --from=build /app/target/newsAggregator-0.0.1-SNAPSHOT.jar app.jar
COPY entrypoint.sh .

# Make the script executable
RUN chmod +x entrypoint.sh

# Set the entrypoint to our new script
ENTRYPOINT ["./entrypoint.sh"]