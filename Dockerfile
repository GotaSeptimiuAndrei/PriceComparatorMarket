FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace/app

COPY pom.xml .
RUN mvn -ntp dependency:go-offline

COPY src ./src
RUN mvn -ntp clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=build /workspace/app/target/PriceComparatorMarket-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
