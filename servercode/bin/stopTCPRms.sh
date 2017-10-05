#!/bin/sh

kill $(ps aux | grep 'rm' | awk '{print $2}')
