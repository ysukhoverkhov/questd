#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CONF=$SCRIPT_DIR/conf
STAGE=$SCRIPT_DIR/target/universal/stage

$STAGE/bin/questd -d $SCRIPT_DIR -Dhttp.port=9000 -Dlogger.file=$CONF/application-logger.xml -Dconfig.file=$CONF/application-questme.conf
