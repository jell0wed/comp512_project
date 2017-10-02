#!/bin/sh

kill $(ps aux | grep '1099 rm' | awk '{print $2}')
