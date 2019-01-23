FROM java:latest
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>
ENV CONFIG_SERVICE_PASSWORD root
ADD ./target/account-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/account-service.jar"]

EXPOSE 6000