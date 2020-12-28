#!/bin/sh

CLASSPATH=out/production/blokus
CLASSPATH=$CLASSPATH:/home/wannamak/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar
CLASSPATH=$CLASSPATH:/home/wannamak/.m2/repository/com/google/protobuf/protobuf-java/3.10.0/protobuf-java-3.10.0.jar

java \
  -Djava.util.logging.config.file=logging.properties \
  -cp $CLASSPATH \
  blokus.PrintGames $@
