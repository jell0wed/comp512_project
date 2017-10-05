#!/bin/sh

export CLASSPATH=$(echo "$(pwd)/../out_gradle/")

rmiregistry
