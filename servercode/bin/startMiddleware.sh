#!/bin/sh

export CLASSPATH=/Users/jpoisson/Desktop/comp512/servercode/:/Users/jpoisson/Desktop/comp512/servercode/src/:/Users/jpoisson/Desktop/comp512/servercode/:/Users/jpoisson/Desktop/comp512/servercode/build/:/Users/jpoisson/Desktop/comp512/servercode/build/classes/main/

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ middleware.impl.tcp.MiddlewareTCPServer
