#!/bin/bash
#JAVA_HOME=
CLASSPATH=batm_server_extensions_test.jar
CLASSPATH=$CLASSPATH:$(echo "../libs"/*.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_api/libs"/*.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_api/dist"/*.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_extra/libs"/*.jar | tr ' ' ':')
echo $CLASSPATH
export LD_LIBRARY_PATH=./lib
java -Djava.library.path=./lib -cp "$CLASSPATH" com.generalbytes.batm.server.extensions.test.Tester $*