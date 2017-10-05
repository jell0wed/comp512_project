#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ middleware.impl.tcp.MiddlewareTCPServer
