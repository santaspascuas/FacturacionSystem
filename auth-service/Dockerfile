FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY src/ src/
RUN ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /app/target/*.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8081 \
    JWT_PRIVATE_KEY_PATH=file:/run/secrets/jwt_private_key
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
