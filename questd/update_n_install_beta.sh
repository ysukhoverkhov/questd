#!/bin/bash

service questd stop

hg pull
hg up -r "0.40.12" --clean
./stage.sh

cp -f ../install/questd_beta.conf /etc/init/

service questd_beta start
