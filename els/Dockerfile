FROM ubuntu:latest AS build

WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-25-jdk curl && \
    rm -rf /var/lib/apt/lists/*

COPY . .

RUN chmod +x ./mvnw

# Build without running tests, so Docker build does not need JWT_SECRET
RUN ./mvnw clean install -U -DskipTests

EXPOSE 8080

ENTRYPOINT ["./mvnw", "spring-boot:run"]