set SCRIPT_DIR=%cd%
set CONF=%SCRIPT_DIR%/conf
set STAGE=%SCRIPT_DIR%/target/universal/stage

%STAGE%/bin/questd -Dhttp.port=9000 -Dlogger.file=%CONF%/application-logger.xml -Dconfig.file=%CONF%/application-questme.conf
