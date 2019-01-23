FROM java:latest
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>
ENV CONFIG_SERVICE_PASSWORD root
ADD ./target/gateway.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/gateway.jar"]

EXPOSE 4000