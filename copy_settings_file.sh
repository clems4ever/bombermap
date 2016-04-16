#!/bin/bash

adb -s "emulator-$1" push default.conf /mnt/sdcard/default.conf

