./gradlew build
./gradlew :server_extensions_test:install
./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test -j server_extensions_extra/build/libs/batm_server_extensions_extra.jar -a get-ebalance -n=digifinex -p=7ad8fd71dd9685:b9a171da71cd9a740a5bba4ebec614173e702ef3b0:usd 