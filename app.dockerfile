FROM java:8

MAINTAINER Charles Uye "charlesdomain@gmail.com"

RUN  \
  export DEBIAN_FRONTEND=noninteractive && \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update && \
  apt-get -y upgrade && \
  apt-get install -y vim wget curl maven

VOLUME /vol/development

RUN mkdir -p /vol/development
WORKDIR /vol/development

CMD ["mvn", "clean", "package", "exec:java"]