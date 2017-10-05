#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java middleware.impl.tcp.MiddlewareTCPServer
