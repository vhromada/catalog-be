FROM eclipse-temurin:25 AS builder
WORKDIR /builder
COPY build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:25-jre
WORKDIR /application
COPY --from=builder --chown=65534:65534 /builder/extracted/dependencies/ ./
COPY --from=builder --chown=65534:65534 /builder/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=65534:65534 /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=65534:65534 /builder/extracted/application/ ./
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
