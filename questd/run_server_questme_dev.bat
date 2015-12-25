set SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
set CONF=%SCRIPT_DIR%/conf
set STAGE=%SCRIPT_DIR%/target/universal/stage

%STAGE%/bin/questd -d %SCRIPT_DIR% -Dhttp.port=9000 -Dlogger.file=%CONF%/application-logger.xml -Dconfig.file=%CONF%/application-questme.conf
