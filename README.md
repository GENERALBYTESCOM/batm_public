BATM Public Repository
===========

This repository contains Bitcoin ATM related code used in BATMOne, BATMTwo and BATM Server products.
More information about the products can be found here: http://www.generalbytes.com


All source code is released under GPL2.

Content
=======
server_extensions_api - contains extension api that all extensions use to extend BATM Server's functionality.
server_extensions_extra - reference extension implementation that implements BTC, LTC and DOGE coin support functionality.

Build information
=================
cd server_extensions_api
ant
cd ..
cd server_extensions_extra
ant
cd ..
cp server_extensions_extra/dist/server_extensions_extra/dist/batm_server_extensions_extra.jar /batm/app/master/extensions/

Note for developers
==========
When you implement support for new crypto-coin add it please to server_extensions_extra this way it will get into default BATM Server installation pack for customers.
