FROM quay.io/quarkus/quarkus-micro-image:latest
WORKDIR /app
COPY target/rating-service-runner runner
COPY target/lib/ lib/
EXPOSE 8083
ENTRYPOINT ["/app/runner"]