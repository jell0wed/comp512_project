#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/home/2015/jpoiss12/comp512_project/servercode/ ResImpl.ResourceManagerRMIImpl 1099 rmCar &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/home/2015/jpoiss12/comp512_project/servercode/ ResImpl.ResourceManagerRMIImpl 1099 rmRoom &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/home/2015/jpoiss12/comp512_project/servercode/ ResImpl.ResourceManagerRMIImpl 1099 rmFlight &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/home/2015/jpoiss12/comp512_project/servercode/ ResImpl.ResourceManagerRMIImpl 1099 rmOther &
