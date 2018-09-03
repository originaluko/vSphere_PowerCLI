#!/bin/sh
# Copyright (c) 2012-2018 VMware, Inc. All rights reserved.
# Mac OS script
# Note: if Ant runs out of memory try defining ANT_OPTS=-Xmx512M

if [ -z "$ANT_HOME" ] || [ ! -f "${ANT_HOME}"/bin/ant ]
then
   echo BUILD FAILED: You must set the environment variable ANT_HOME to your Apache Ant folder
   exit 1
fi

if [ -z "$VSPHERE_SDK_HOME" ] || [ ! -f "${VSPHERE_SDK_HOME}"/libs/vsphere-client-lib.jar ]
then
   echo BUILD FAILED: You must set the environment variable VSPHERE_SDK_HOME to your vSphere Client SDK folder
   exit 1
fi

"${ANT_HOME}"/bin/ant -f build-plugin-package.xml

exit 0
