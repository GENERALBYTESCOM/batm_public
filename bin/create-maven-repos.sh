#!/bin/bash
directory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $directory

# API dependencies
./install-lib.sh api mail.jar javax.mail mail 1.4.7
./install-lib.sh api slf4j-api-1.7.5.jar org.slf4j slf4j-api 1.7.5
./install-lib.sh api slf4j-simple-1.7.5.jar org.slf4j slf4j-simple 1.7.5

# EXTRA dependencies
./install-lib.sh extra base64-2.3.8.jar net.iharder base64 2.3.8
./install-lib.sh extra bitcoin-json-rpc-client-1.0.jar com.azazar.bitcoin.jsonrpcclient bitcoin-json-rpc-client 1.0
./install-lib.sh extra commons-io-2.4.jar commons-io commons-io 2.4
./install-lib.sh extra guava-18.0.jar com.google.guava guava 18.0
./install-lib.sh extra jackson-annotations-2.4.0.jar com.fasterxml.jackson.core jackson-annotations 2.4.0
./install-lib.sh extra jackson-core-2.4.0.jar com.fasterxml.jackson.core jackson-core 2.4.0
./install-lib.sh extra jackson-databind-2.4.0.jar com.fasterxml.jackson.core jackson-databind 2.4.0
./install-lib.sh extra javax.ws.rs-api-2.0.1.jar javax.ws.rs javax.ws.rs-api 2.0.1
./install-lib.sh extra rescu-1.7.2-SNAPSHOT.jar com.github.mmazi rescu 1.7.2-SNAPSHOT
./install-lib.sh extra xchange-core-4.0.1-SNAPSHOT.jar org.knowm.xchange xchange-core 4.0.1-SNAPSHOT
./install-lib.sh extra xchange-bitfinex-4.0.1-SNAPSHOT.jar org.knowm.xchange xchange-bitfinex 4.0.1-SNAPSHOT
./install-lib.sh extra xchange-itbit-4.0.1-SNAPSHOT.jar org.knowm.xchange xchange-itbit 4.0.1-SNAPSHOT
./install-lib.sh extra xchange-bittrex-4.0.1-SNAPSHOT.jar org.knowm.xchange xchange-bittrex 4.0.1-SNAPSHOT
./install-lib.sh extra xchange-poloniex-4.0.1-SNAPSHOT.jar org.knowm.xchange xchange-poloniex 4.0.1-SNAPSHOT

# EXTRA/test dependencies
./install-lib.sh extra test/commons-logging-1.2.jar commons-logging commons-logging 1.2
./install-lib.sh extra test/fluent-hc-4.5.2.jar org.apache.httpcomponents fluent-hc 4.5.2
./install-lib.sh extra test/hamcrest-all-1.3.jar org.hamcrest hamcrest-all 1.3
./install-lib.sh extra test/httpclient-4.5.2.jar org.apache.httpcomponents httpclient 4.5.2
./install-lib.sh extra test/httpcore-4.4.5.jar org.apache.httpcomponents httpcore 4.4.5
./install-lib.sh extra test/javax.servlet-api-3.1.0.jar javax.servlet javax.servlet-api 3.1.0
./install-lib.sh extra test/jcommander-1.48.jar com.beust jcommander 1.48
./install-lib.sh extra test/jetty-6.1.26.jar org.mortbay.jetty jetty 6.1.26
./install-lib.sh extra test/jetty-util-6.1.26.jar org.mortbay.jetty jetty-util 6.1.26
./install-lib.sh extra test/json-20160212.jar org.json json 20160212
./install-lib.sh extra test/jsonassert-1.3.0.jar org.skyscreamer jsonassert 1.3.0
./install-lib.sh extra test/junit-4.12.jar junit junit 4.12
./install-lib.sh extra test/rest-assured-2.9.0.jar com.jayway.restassured rest-assured 2.9.0
./install-lib.sh extra test/testng-6.9.10.jar org.testng testng 6.9.10
./install-lib.sh extra test/wiremock-1.58.jar com.github.tomakehurst wiremock 1.58
./install-lib.sh extra test/xmlunit-1.6.jar xmlunit xmlunit 1.6








# TEST dependencies
./install-lib.sh test jopt-simple-4.9.jar net.sf.jopt-simple jopt-simple 4.9
