# SSH TUNNEL CLIENT

This client application can be used to simplify ssh tunnel creation from BATM server to your node.
Application is listening on port 2222 and is waiting for BATM server connection.
Using ssh tunnels is recomended when BATM server wants to communicate to bitcoind or similar node as it adds additional layer of encryption.


To Install (tested on ubuntu server 18.04):

0. Install dependencies
```
apt install unzip zip
```
1. Install Java 8 (used for compilation of client and running)
```
curl -s "https://get.sdkman.io" | bash
source "/root/.sdkman/bin/sdkman-init.sh"
sdk install java  8.0.232.hs-adpt
```
2. Clone this repository to download source code
```
git clone https://github.com/GENERALBYTESCOM/batm_public.git
```
3. Compile application from sources
```
cd batm_public
./gradlew build
```
4. Install service making sure it runs after start of the server
```
cd batm_ssh_tunnel
./batm_ssh_tunnel_install.sh
```


