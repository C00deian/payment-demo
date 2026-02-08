FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN chmod +x mvnw \
  && ./mvnw -q -DskipTests package \
  && JAR_FILE="$(ls target/*.jar | grep -v '\\.original$' | head -n 1)" \
  && cp "$JAR_FILE" /tmp/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /tmp/app.jar /app/app.jar

EXPOSE 8081
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
