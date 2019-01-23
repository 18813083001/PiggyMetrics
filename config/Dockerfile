FROM java:latest
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>
ENV CONFIG_SERVICE_PASSWORD root
ENV ACCOUNT_SERVICE_PASSWORD root
ENV NOTIFICATION_SERVICE_PASSWORD root
ENV STATISTICS_SERVICE_PASSWORD root
ADD ./target/config.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/config.jar"]

HEALTHCHECK --interval=10s --timeout=3s CMD curl -f http://localhost:8888/health || exit 1

EXPOSE 8888