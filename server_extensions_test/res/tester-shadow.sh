#!/bin/bash

CLASSPATH=$(echo "../../server_extensions_test/target"/*-jar-with-dependencies.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_api/target"/*-jar-with-dependencies.jar | tr ' ' ':')
CLASSPATH=$CLASSPATH:$(echo "../../server_extensions_extra/target"/*-jar-with-dependencies.jar | tr ' ' ':')

echo $CLASSPATH
export LD_LIBRARY_PATH=.
java -Djava.library.path=. -cp "$CLASSPATH" com.generalbytes.batm.server.extensions.test.Tester $*