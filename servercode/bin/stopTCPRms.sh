#!/bin/sh

kill $(ps aux | grep 'Impl rm' | awk '{print $2}')
