FROM docker-proxy.tools.v2.symmetrical.ai/bellsoft/liberica-openjre-alpine:17.0.8.1-1

ARG GROUP="sym-group"
ARG USER="sym-user"
ARG JAR_PATH="."

RUN addgroup -g 1111 $GROUP && adduser -u 1111 --disabled-password -G $GROUP $USER
USER $USER
WORKDIR /home/$USER
EXPOSE 8080 8090
ENTRYPOINT ["java", "-jar", "./app.jar"]
COPY --chown=$USER:$GROUP $JAR_PATH/app.jar ./app.jar
