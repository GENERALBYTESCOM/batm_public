./gradlew build
./gradlew :server_extensions_test:install
./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test -j server_extensions_extra/build/libs/batm_server_extensions_extra.jar -a get-ebalance -n=digifinex -p=7f8624f6fa24bd:497c32b2325d5687cb5cfd5ba52f4282d45634ff12:usd 