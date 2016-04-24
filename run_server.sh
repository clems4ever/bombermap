#!/bin/bash

export AMQP_VIRTUAL_HOST=/$1

echo "AMQP_VIRTUAL_HOST=$AMQP_VIRTUAL_HOST"

nodejs game_controller/app.js &
nodejs game_controller/clock.js --manual

