#!/bin/bash

service questd_questme stop

hg pull
hg up -r "0.50.03" --clean
./stage.sh

cp -f ../install/questd_questme.conf /etc/init/

service questd_questme start
