FROM java:latest
ADD target/*.jar app.jar
EXPOSE 9082
ENTRYPOINT ["java","-jar","/app.jar"]

