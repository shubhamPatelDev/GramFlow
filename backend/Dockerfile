FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Xmx512m"
RUN ./gradlew build -x test --no-daemon
RUN find build/libs -name "*.jar" -not -name "*-plain.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
