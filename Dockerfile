FROM openjdk:alpine

LABEL maintainer "Jeff Sippel <jsippel@acorns.com>"

RUN apk update && apk upgrade

RUN addgroup -g 9000 -S code && \
  adduser -S -G code app
USER app

COPY codeclimate-scalastyle-assembly-0.1.0.jar /usr/src/app/engine-core.jar
COPY src/main/resources/docker /usr/src/app

WORKDIR /code
VOLUME /code

CMD ["/usr/src/app/bin/scalastyle"]
