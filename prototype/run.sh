#!/bin/sh

APP_MAIN="com.redhat.mq.jmx.Agent"
CLIENT_DIST_DIR=jmx/dist

CLASSPATH="jmx/dist/mq-jmx.jar"
CLASSPATH="${CLASSPATH}:lib/com.ibm.mq.jar"
CLASSPATH="${CLASSPATH}:lib/com.ibm.mq.headers.jar"
CLASSPATH="${CLASSPATH}:lib/com.ibm.mq.pcf.jar"
CLASSPATH="${CLASSPATH}:lib/commons-codec-1.7.jar"

java -cp ${CLASSPATH} ${JAVA_OPTS} ${APP_MAIN}
