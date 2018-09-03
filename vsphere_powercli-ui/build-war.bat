@echo off
REM --- Copyright (c) 2012-2018 VMware, Inc. All rights reserved.
REM --- Windows script
REM --- (if Ant runs out of memory try defining ANT_OPTS=-Xmx512M)

@setlocal
@IF not defined ANT_HOME (
   @echo BUILD FAILED: You must set the env variable ANT_HOME to your Apache Ant folder
   goto end
)
@IF not defined VSPHERE_SDK_HOME (
   @echo BUILD FAILED: You must set the env variable VSPHERE_SDK_HOME to your vSphere Client SDK folder
   goto end
)
@IF not exist "%VSPHERE_SDK_HOME%\libs\vsphere-client-lib.jar" (
   @echo BUILD FAILED: VSPHERE_SDK_HOME is not set to a valid vSphere Client SDK folder
   @echo %VSPHERE_SDK_HOME%\libs\vsphere-client-lib.jar is missing
   goto end
)

@call "%ANT_HOME%\bin\ant" -f build-war.xml

:end
