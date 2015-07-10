FROM java

RUN apt-get update
RUN apt-get install -y ruby ruby-nokogiri

RUN adduser --uid 9000 --disabled-password --quiet --gecos "" app
USER app

WORKDIR /home/app

COPY scalastyle_config.xml /home/app/
COPY scalastyle_2.11-0.6.0-batch.jar /home/app/

COPY . /home/app

CMD ["/home/app/bin/scalastyle"]
