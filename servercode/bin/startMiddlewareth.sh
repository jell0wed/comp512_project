#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/theoszymkowiak/Desktop/McGill/Courses/COMP512/Workspace/comp512_project/servercode/ middleware.impl.rmi.MiddlewareRMIServer
