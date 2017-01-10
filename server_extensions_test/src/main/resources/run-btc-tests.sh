#!/bin/bash
SERVER_EXTENSIONS=../../server_extensions_extra/dist/batm_server_extensions_extra.jar
./tester.sh -j $SERVER_EXTENSIONS -a list-ratesources
./tester.sh -j $SERVER_EXTENSIONS -a list-wallets
./tester.sh -j $SERVER_EXTENSIONS -a list-exchanges
./tester.sh -j $SERVER_EXTENSIONS -a list-paymentprocessors
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n yahoofinance -p USD
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n yahoofinance -p EUR
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n bitfinex -p USD
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n bitfinex -p EUR
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n itbit -p USD
./tester.sh -j $SERVER_EXTENSIONS -a get-rates -n itbit -p EUR
