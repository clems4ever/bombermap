#!/bin/bash

port=$1

parislat=2.3228797
parislong=48.8870407
# 2.3228797 48.8870407

while [ 1 ]
do
#latitude=$(( ($parislat + RANDOM % 10 )  + 1 ))
#longitude=$(( ($parislong + RANDOM % 50 )  + 1 ))

#echo $latitude
#cecho $longitude

# 5554
g++ generatecoords.cpp -o geogen
chmod +x geogen

./geogen
./geogen | telnet localhost $port 
sleep 1
done
