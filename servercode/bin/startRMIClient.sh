#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Workspaces/McGill/comp512_project/servercode clients.RMIInteractiveClient cs-1.cs.mcgill.ca 1099 rmMiddleware
