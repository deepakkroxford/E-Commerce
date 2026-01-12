# Use OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine

# Set work directory
WORKDIR /app

# Copy built jar
COPY target/eCommerce-0.0.1-SNAPSHOT.jar eCommerce-0.0.1-SNAPSHOT.jar

# Expose port
EXPOSE 9090

# Run the app
ENTRYPOINT ["java", "-jar", "eCommerce-0.0.1-SNAPSHOT.jar"]
