FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/Blogs-0.0.1-SNAPSHOT.jar Blogs-v1.0.jar

EXPOSE 1000

ENTRYPOINT ["java", "-jar", "Blogs-v1.0.jar"]

