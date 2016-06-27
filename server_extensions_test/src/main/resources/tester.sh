#!/bin/bash
#JAVA_HOME=
CLASSPATH=batm_server_extensions_test.jar
#CLASSPATH=$CLASSPATH:$(echo "../libs"/*.jar | tr ' ' ':')
#CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_api/libs"/*.jar | tr ' ' ':')
#CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_api/dist"/*.jar | tr ' ' ':')
#CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_extra/libs"/*.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(find ../libs -name '*.jar' | xargs echo | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(find ../../server_extensions_api/libs -name '*.jar' | xargs echo | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(find ../../server_extensions_api/dist -name '*.jar' | xargs echo | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(find ../../server_extensions_extra/libs -name '*.jar' | xargs echo | tr ' ' ':')
echo $CLASSPATH
export LD_LIBRARY_PATH=./lib
java -Djava.library.path=./lib -cp "$CLASSPATH" com.generalbytes.batm.server.extensions.test.Tester $*