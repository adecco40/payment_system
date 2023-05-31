FROM openjdk:11
ADD target/payment_system-0.0.1-SNAPSHOT.jar payment_system-0.0.1-SNAPSHOT.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "payment_system-0.0.1-SNAPSHOT.jar"]
