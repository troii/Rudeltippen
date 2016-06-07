FROM java:7
MAINTAINER Thomas Einwaller, tom@troii.com

RUN cd /opt && wget https://downloads.typesafe.com/play/1.2.7.2/play-1.2.7.2.zip && unzip play-1.2.7.2.zip && rm play-1.2.7.2.zip

ADD rudeltippen.zip /opt/
RUN mkdir /opt/rudeltippen && unzip /opt/rudeltippen.zip -d /opt/rudeltippen

WORKDIR /opt/rudeltippen

CMD /opt/play-1.2.7.2/play run --%prod

EXPOSE 9000
