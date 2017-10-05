#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java clients.TCPInteractiveClient localhost 8080