#!/bin/bash

port=$1
latitude=$2
longitude=$3

# 5554
# 2.3228797 48.8870407

echo "$latitude $longitude $port"

echo "geo fix $latitude $longitude" | telnet localhost $port 

