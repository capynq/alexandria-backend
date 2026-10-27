FROM gradle:8.5-jdk17@sha256:7704366590930c03de7e514008ba3d7b7031b92591bd5a74fae79c16f3a17726 AS build
WORKDIR /workspace
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY config config
COPY src src
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:17-jre-jammy@sha256:c35794d1348777e6bc310b9b7f420098ef8f37e988199549b545fded79a895a2
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build --chown=app:app /workspace/build/libs/alexandria-0.0.3.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
