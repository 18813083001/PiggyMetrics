FROM java:latest
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>
ENV CONFIG_SERVICE_PASSWORD root
ADD ./target/notification-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/notification-service.jar"]

EXPOSE 8000