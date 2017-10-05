#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

java ResImpl.ResourceManagerTCPImpl rmCar 10001 &
java ResImpl.ResourceManagerTCPImpl rmFlight 10002 &
java ResImpl.ResourceManagerTCPImpl rmRoom 10003 &
java ResImpl.ResourceManagerTCPImpl rmOther 10004 &