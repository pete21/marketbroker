ARG VERSION=21.0.3-10
ARG APP_PATH="/usr/app"

FROM bellsoft/liberica-openjdk-alpine:${VERSION} as BUILD
WORKDIR $APP_PATH
COPY . .
RUN ./gradlew

FROM bellsoft/liberica-openjre-alpine:${VERSION}

ARG GROUP="piotr-group"
ARG USER="piotr-user"

ARG JAR_NAME="marketbroker-0.1.0-SNAPSHOT.jar"

RUN addgroup -g 1111 $GROUP && adduser -u 1111 --disabled-password -G $GROUP $USER
USER $USER
WORKDIR /home/$USER
EXPOSE 8080 8090
COPY --from=BUILD --chown=$USER:$GROUP $APP_PATH/build/libs/$JAR_NAME ./app.jar
ENTRYPOINT ["java", "-jar", "./app.jar"]
