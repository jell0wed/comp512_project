#!/bin/sh

#export CLASSPATH=/Users/jpoisson/Desktop/comp512/servercode/:/Users/jpoisson/Desktop/comp512/servercode/src/:/Users/jpoisson/Desktop/comp512/servercode/:/Users/jpoisson/Desktop/comp512/servercode/build/:/Users/jpoisson/Desktop/comp512/servercode/build/classes/main/
export CLASSPATH=/Users/jpoisson/Desktop/comp512/servercode/build/classes/main/
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ ResImpl.ResourceManagerImpl 1099 rmCar &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ ResImpl.ResourceManagerImpl 1099 rmRoom &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ ResImpl.ResourceManagerImpl 1099 rmFlight &
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:/Users/jpoisson/Desktop/comp512/servercode/ ResImpl.ResourceManagerImpl 1099 rmOther &
