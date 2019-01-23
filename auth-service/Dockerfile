FROM java:latest
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>
ENV CONFIG_SERVICE_PASSWORD root
ADD ./target/auth-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/auth-service.jar"]

EXPOSE 5000