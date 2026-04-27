# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar Blogs-v1.0.jar

EXPOSE 1000
ENTRYPOINT ["java", "-jar", "Blogs-v1.0.jar"]