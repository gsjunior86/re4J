#FROM adoptopenjdk:11.0.11_9-jdk-hotspot
FROM jmonkeyengine/buildenv-jme3:base
RUN apt-get update
RUN apt-get install -y libx11-dev
RUN apt-get install -y libxrender1 libxtst6 libxi6
RUN apt-get install -y libxcursor-dev
RUN apt-get install -y libxrandr-dev
RUN apt-get install -y libxxf86vm-dev

RUN apt-get install -y liblwjgl-java


COPY ./dist/libopenal64.so /game/libopenal64.so
COPY ./dist/liblwjgl64.so /game/liblwjgl64.so
COPY ./dist/libbulletjme.so /game/libbulletjme.so
COPY ./dist/RE4j.jar /game/RE4j.jar
COPY ./dist/lib /game/lib


WORKDIR /game


ENTRYPOINT java -jar RE4j.jar
