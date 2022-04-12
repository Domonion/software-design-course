FROM maven:3-openjdk-17 AS build
COPY . /home/maven/src
WORKDIR /home/maven/src
RUN mvn clean
RUN mvn package -Dmaven.test.skip

FROM openjdk:17
EXPOSE 8081:8081
RUN mkdir /app
COPY --from=build /home/maven/src/exchange-app/target/*-with-dependencies.jar /app/exchange-app.jar
ENTRYPOINT ["java","-jar","/app/exchange-app.jar"]