#!/bin/bash

if [ "$WARGAME_CLIENT_DIRECTORY" == "" ]
then
	echo "WARGAME_CLIENT_DIRECTORY not set"
        exit 1
fi

docker run --name wargame_devenv --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v "$WARGAME_CLIENT_DIRECTORY:/data" registry.ingenious-cm.fr/wargame_devenv /opt/android-studio/bin/studio.sh
