#!/bin/bash

port=$1


while [ 1 ]
do
latitude=$(( ( RANDOM % 10 )  + 1 ))
longitude=$(( ( RANDOM % 50 )  + 1 ))

echo $latitude
echo $longitude

# 5554
# 2.3228797 48.8870407

echo "geo fix $latitude $longitude" | telnet localhost $port 
sleep 1
done
