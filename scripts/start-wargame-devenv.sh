#!/bin/bash

if [ "$WARGAME_CLIENT_DIRECTORY" == "" ]
then
	echo "WARGAME_CLIENT_DIRECTORY not set"
        exit 1
fi

if [ "$ANDROID_SDK_DIRECTORY" != "" ]
then
	ANDROID_SDK_DIRECTORY_FLAGS="-v $ANDROID_SDK_DIRECTORY:/home/developer/Android"
	echo "ANDROID_SDK=$ANDROID_SDK_DIRECTORY"
fi

set -x
docker run --privileged --name wargame_devenv --rm -e SHELL=/bin/bash -e DISPLAY=$DISPLAY -v /dev/kvm:/dev/kvm -v /tmp/.X11-unix:/tmp/.X11-unix $ANDROID_SDK_DIRECTORY_FLAGS -v "$WARGAME_CLIENT_DIRECTORY:/data" registry.ingenious-cm.fr/wargame_devenv /opt/android-studio/bin/studio.sh
