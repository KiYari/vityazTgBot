FROM openjdk:17
MAINTAINER vityaz.ru
COPY build/libs/bot-1.0-plain.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]