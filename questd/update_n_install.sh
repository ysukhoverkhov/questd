#!/bin/bash

hg pull
hg up -r "0.40.04 Everything is open"

./stage.sh

service questd stop

cp -f ../install/questd.conf /etc/init/

service questd start
