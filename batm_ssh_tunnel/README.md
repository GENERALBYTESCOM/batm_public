# SSH TUNNEL CLIENT

This client application can be used to simplify ssh tunnel creation from BATM server to your node.
Application is listening on port 22222 and is waiting for BATM server connection.
Using ssh tunnels is recomended when BATM server wants to communicate to bitcoind or similar node as it adds additional layer of encryption.


## To Install (tested on ubuntu server 24.04):

0. Install Java 17 (used for compilation of client and running)
```
apt update
apt install openjdk-17-jdk-headless
```
1. Clone this repository to download source code
```
git clone https://github.com/GENERALBYTESCOM/batm_public.git
```
2. Compile application from sources
```
cd batm_public
./gradlew build
```
3. Install service making sure it runs after start of the server
```
cd batm_ssh_tunnel
./batm_ssh_tunnel_install.sh
```


