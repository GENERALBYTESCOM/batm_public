./gradlew build
./gradlew :server_extensions_test:install
#./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test -j server_extensions_extra/build/libs/batm_server_extensions_extra.jar -a get-rates -n=bkexRateSource -p=10f0d979ad57d97de200f35cee3184118616476c4e711ad22024f2e81c0ffb0b:9815ea688ad1fd8e568a374866056847ea7059afcd207763a3a4b70873e8168c:usdt 
./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test -j server_extensions_extra/build/libs/batm_server_extensions_extra.jar -a get-rates -n=bkexRateSource -p=usdt

#digifinex
#key:
#7ad8fd71dd9685
#secret:b9a171da71cd9a740a5bba4ebec614173e702ef3b0

#bkex
#Access Key：
#10f0d979ad57d97de200f35cee3184118616476c4e711ad22024f2e81c0ffb0b
#Secret Key：
#9815ea688ad1fd8e568a374866056847ea7059afcd207763a3a4b70873e8168c