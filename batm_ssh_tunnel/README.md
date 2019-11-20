This client application can be used to simplify ssh tunnel creation to BATM server
Application listening on port 2222 and waiting for BATM server connection.

To Install:
1. Clone this repository
2. Install java 8 
3. Compile application using gradle
4. Install service

Steps:
``apt install unzip zip 
curl -s "https://get.sdkman.io" | bash
source "/root/.sdkman/bin/sdkman-init.sh"
sdk install java  8.0.232.hs-adpt
git clone https://github.com/GENERALBYTESCOM/batm_public.git
cd batm_public
./gradlew build
cd batm_ssh_tunnel
./batm_ssh_tunnel_install.sh
``


