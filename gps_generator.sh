#!/bin/bash

port=$1
latoffset=$2
longoffset=$3

parislat=2.3228797
parislong=48.8870407
# 2.3228797 48.8870407
g++ generatecoords.cpp -o geogen
chmod +x geogen

if [ -z "$2" ]
  then
    echo "now you can set the offset of a player from basic position :"
    echo "try ./gps_generator.sh 5554 0 0 and ./gps_generator.sh 5556 20 20   "

else

while [ 1 ]
do
#latitude=$(( ($parislat + RANDOM % 10 )  + 1 ))
#longitude=$(( ($parislong + RANDOM % 50 )  + 1 ))

#echo $latitude
#cecho $longitude

# 5554

./geogen $latoffset $longoffset
./geogen $latoffset $longoffset | telnet localhost $port 
sleep 1
done
fi
