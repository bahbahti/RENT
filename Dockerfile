FROM maven:3.5.2-jdk-8-alpine AS stageofjar
WORKDIR /springApplication/
COPY . .
RUN mvn clean package


FROM openjdk:12-alpine
COPY --from=stageofjar /springApplication/target/demo-0.0.1-SNAPSHOT.jar .

CMD ["java" , "-jar", "/demo-0.0.1-SNAPSHOT.jar"]
