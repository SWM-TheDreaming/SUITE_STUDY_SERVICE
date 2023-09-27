FROM openjdk:11
ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} suite-study.jar
ENTRYPOINT ["java","-jar","/suite-study.jar"]