# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build

ENV GRADLE_HOME=/opt/gradle \
    GRADLE_USER_HOME=/cache/.gradle \
    GRADLE_OPTS="-Dorg.gradle.daemon=false \
    -Dorg.gradle.parallel=true \
    -Dorg.gradle.caching=true \
    -Xmx2g"

ARG GRADLE_VERSION=8.10
RUN apt-get update && apt-get install -y --no-install-recommends unzip \
    && wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && apt-get remove -y unzip \
    && rm -rf /var/lib/apt/lists/*

ENV PATH="${GRADLE_HOME}/bin:${PATH}"

WORKDIR /app

COPY build.gradle.kts ./
COPY settings.gradle.kts ./

RUN --mount=type=cache,target=/cache/.gradle \
    gradle dependencies --no-daemon --stacktrace

COPY . ./

RUN --mount=type=cache,target=/cache/.gradle \
    gradle :application:bootJar -x test --no-daemon --stacktrace --build-cache

FROM eclipse-temurin:21-jre AS runtime

RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -m -d /app -s /bin/false appuser

WORKDIR /app

COPY --from=build --chown=appuser:appgroup /app/application/build/libs/application.jar app.jar

HEALTHCHECK --interval=30s \
            --timeout=5s \
            --retries=3 \
#            CMD wget -qO- http://localhost:8090/health/
            CMD curl --fail --silent http://localhost:8090/health | grep UP || exit 1

EXPOSE 8080 8090

ENV JAVA_OPTS="-server \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=25.0 \
    -XX:+UseG1GC \
    -Djava.security.egd=file:/dev/./urandom"

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
