#!/bin/bash

adb -s "emulator-$1" pull /mnt/sdcard/comm.dump comm-$1.dump

