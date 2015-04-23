#!/bin/bash

hg pull
hg up -r "\"$1\""

./stage.sh
