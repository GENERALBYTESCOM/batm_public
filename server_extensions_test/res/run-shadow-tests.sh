#!/usr/bin/env bash

printf "\n\n### -a list-ratesources ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a list-ratesources
printf "\n\n### -a list-wallets ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a list-wallets
printf "\n\n### -a list-exchanges ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a list-exchanges
printf "\n\n### -a list-paymentprocessors ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a list-paymentprocessors
printf "\n\n### -a get-rates -n sdcpoloniexrs -p USD ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-rates -n sdcpoloniexrs -p USD
printf "\n\n### -a get-rates -n sdcpoloniexrs -p EUR ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-rates -n sdcpoloniexrs -p EUR

#printf "\n\n### -a get-rates -n sdcbittrexrs -p USD ###\n\n"
#./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-rates -n sdcbittrexrs -p USD
#printf "\n\n### -a get-rates -n sdcbittrexrs -p EUR ###\n\n"
#./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-rates -n sdcbittrexrs -p EUR

printf "\n\n### -a get-wbalance -n shadowcoind -p protocol:user:password:host:port:accountname ###\n\n"
./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-wbalance -n shadowcoind -p http:testuser:testpassword:localhost:51736

#printf "\n\n### -a get-ebalance -n bittrex -p userId:walletId:apikey:apisecret:fiatcurrency ###\n\n"
#./tester-shadow.sh -j ../../server_extensions_extra/target/batm-server-extensions-extra-0.1-SNAPSHOT-jar-with-dependencies.jar -a get-ebalance -n sdcpoloniexrs -p EUR
