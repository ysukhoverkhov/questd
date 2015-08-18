#!/bin/bash

service questd stop

hg pull
hg up -r "0.40.10" --clean
./stage.sh

cp -f ../install/questd.conf /etc/init/

service questd start
