FROM openjdk:11

WORKDIR /

RUN apt-get -y update; apt-get -y install jq # required for curl

RUN mkdir /logs
RUN mkdir /conf

ADD ./target/tourist-bot-1.0.jar /tourist-bot-1.0.jar

ADD docker-entrypoint.sh /docker-entrypoint.sh

RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
