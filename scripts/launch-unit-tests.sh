#!/bin/bash


if [ "$WARGAME_CLIENT_DIRECTORY" == "" ]
then
	echo "WARGAME_CLIENT_DIRECTORY not set"
        exit 1
fi

if [ "$BUILD_DIRECTORY" == "" ]
then
	BUILD_DIRECTORY="`pwd`"
fi


set -x

docker run --rm -e ANDROID_HOME=/home/developer/Android/Sdk -v "$WARGAME_CLIENT_DIRECTORY:/data" -v "$BUILD_DIRECTORY:/build" -e TERM=dumb registry.ingenious-cm.fr/wargame_devenv /bin/bash -c "cd /data; ./gradlew jacocoTestReport; TEST_RESULTS=\$?; cp -r /data/app/build/* /build; exit \$TEST_RESULTS"
