#!/bin/bash

latitude=$1
longitude=$2

# 2.3228797 48.8870407

echo "geo fix $latitude $longitude" | telnet localhost 5554
