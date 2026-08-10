# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY enigma-logic enigma-logic
COPY enigma-dal enigma-dal
COPY enigma-core enigma-core
COPY enigma-api enigma-api
COPY enigma-app enigma-app
RUN mvn -q clean package -DskipTests

# ---- run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/enigma-app/target/enigma-chat.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
