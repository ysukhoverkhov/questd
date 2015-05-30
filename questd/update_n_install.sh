#!/bin/bash

hg pull
hg up -r "0.40.05"

./stage.sh

service questd stop

cp -f ../install/questd.conf /etc/init/

service questd start
