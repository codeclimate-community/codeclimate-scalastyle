FROM bigtruedata/sbt:0.13.15-2.12.2 AS builder
WORKDIR /code
ADD project/ /code/project
ADD build.sbt /code

## force sbt to load dependencies and compile interface
RUN mkdir -p src/main/scala && \
    echo "object Main {}" > src/main/scala/Main.scala && \
    sbt compile  && rm -rf src

## copy source code and create an assembly jar
COPY . /code
RUN sbt assembly

# Build main image
FROM openjdk:alpine

LABEL maintainer "Ivan Luzyanin <ivan@acorns.com>"
LABEL maintainer "Jeff Sippel <jsippel@acorns.com>"

RUN apk update && apk upgrade

RUN addgroup -g 9000 -S code && \
  adduser -S -G code app
USER app

COPY --from=builder /code/target/scala-2.12/codeclimate-scalastyle-assembly-*.jar /usr/src/app/engine-core.jar
COPY src/main/resources/docker /usr/src/app
COPY src/main/resources/docker/engine.json /

WORKDIR /code
VOLUME /code

CMD ["/usr/src/app/bin/scalastyle"]
