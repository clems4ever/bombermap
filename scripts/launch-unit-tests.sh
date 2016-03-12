#!/bin/bash


if [ "$WARGAME_CLIENT_DIRECTORY" == "" ]
then
	echo "WARGAME_CLIENT_DIRECTORY not set"
        exit 1
fi

if [ "$RESULTS_DIRECTORY" == "" ]
then
	RESULTS_DIRECTORY="`pwd`"
fi


set -x

docker run --rm -e ANDROID_HOME=/home/developer/Android/Sdk -v "$WARGAME_CLIENT_DIRECTORY:/data" -v "$RESULTS_DIRECTORY:/test-results" -e TERM=dumb registry.ingenious-cm.fr/wargame_devenv /bin/bash -c "cd /data && ./gradlew test; cp -r /data/app/build/test-results/debug/* /test-results"
