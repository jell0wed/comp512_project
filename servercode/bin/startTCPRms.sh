#!/bin/sh

export CLASSPATH=/Users/jpoisson/Desktop/comp512/servercode/build/classes/main/

java ResImpl.ResourceManagerTCPImpl rmCar 10001 &
java ResImpl.ResourceManagerTCPImpl rmFlight 10002 &
java ResImpl.ResourceManagerTCPImpl rmRoom 10003 &
java ResImpl.ResourceManagerTCPImpl rmOther 10004 &