FROM maven:3.8.4-jdk-11-slim as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -DskipTests
FROM openjdk:11-jre-slim
RUN mkdir -p /usr/local/lib/target
COPY src/main/resources/aggregations.txt /usr/local/lib/target/aggregations.txt
COPY src/main/resources/create_svg.py /usr/local/lib/target/create_svg.py
COPY --from=build /home/app/target/graphBackend-0.0.1-SNAPSHOT.jar /usr/local/lib/demo.jar
RUN apt update && apt install -y python3 python3-pip python-dev && pip3 install pm4py && pip3 install pandas
RUN apt install -y graphviz
EXPOSE 8080
WORKDIR /usr/local/lib
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]