ARG VERSION=21.0.3-10

FROM gradle:8.7.0-jdk21-alpine AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
WORKDIR /home/gradle/src
COPY ./repository /root/.m2/repository
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle clean build -i --stacktrace


FROM gradle:8.7.0-jdk21-alpine AS build
#COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
WORKDIR /home/gradle/src
COPY ./repository /root/.m2/repository
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle clean bootJar


FROM bellsoft/liberica-openjre-alpine:${VERSION}

ARG GROUP="piotr-group"
ARG USER="piotr-user"

RUN addgroup -g 1111 $GROUP && adduser -u 1111 --disabled-password -G $GROUP $USER
USER $USER
WORKDIR /home/$USER
COPY ./curl-linux-x86_64-glibc-8.18.0.tar.xz .
RUN unxz curl-linux-x86_64-glibc-8.18.0.tar.xz & \
    tar -xf curl-linux-x86_64-glibc-8.18.0.tar & \
    rm curl-linux-x86_64-glibc-8.18.0.tar
EXPOSE 8080 8090
HEALTHCHECK --interval=20s \
            --timeout=10s \
            CMD curl -f http://localhost:8090/health || exit 1
COPY --from=build --chown=$USER:$GROUP /home/gradle/src/application/build/libs/*.jar ./
ENTRYPOINT ["java", "-jar", "./application.jar"]
