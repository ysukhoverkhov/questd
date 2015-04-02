#!/bin/bash

hg pull
hg up $1

./stage.sh
