#!/bin/bash

docker run --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v $WARGAME_CLIENT_DIRECTORY:/data -e TERM=dumb registry.ingenious-cm.fr/wargame_devenv /bin/bash -c "cd /data && ./gradlew test"
