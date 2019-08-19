FROM maven:3.3.9
MAINTAINER "bogdanshishkin1998@gmail.com"
RUN mkdir app
WORKDIR /app
COPY src src
COPY pom.xml pom.xml
RUN mvn clean package
RUN mv target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar app.jar
