#!/bin/bash

service questd_beta stop

hg pull
hg up -r "0.40.13" --clean
./stage.sh

cp -f ../install/questd_beta.conf /etc/init/

service questd_beta start
