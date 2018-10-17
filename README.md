BATM Public Repository - Open Extensions
===========

This repository contains Bitcoin ATM related code used in BATMTwo, BATMThree, BATM Server (CAS) products.

More information about the products can be found here: http://www.generalbytes.com


All source code is released under GPL2.

Architecture
========
![Architecture](https://raw.githubusercontent.com/GENERALBYTESCOM/batm_public/master/doc/open_extensions.png)



Overview
========
Operators operating BATM Server often request feature X to be added on the server. Typically it is adding support for crypto currency XYZ or digital asset exchange XY. 
Purpose of this project is to give operators power to add so needed features themselfs by **extending** BATM Server via **Extensions** mechanism.
All it takes is to find some Java developer and create/implement an extension.

BATM Server on start scans /batm/app/master/extensions/ folder for all files that have .jar extension.
From each jar file server parses batm-extensions.xml file to find out what extensions are present in jar package.

<a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtension.java">Extension</a> is a high level component - something like a plugin - that encapsulates and instantiates rest of the features.

Extension can be asked to provide wallet X for currency Y etc. Best way to learn more about extensions is to read code and find out how other pople implemented support for their wallet or cryptocurrency in module <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra">server_extensions_extra</a> and to see some of very few code <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/examples">examples</a>.

Here is the list of some functionality that can be extended using Extensions API:
* **Implement support for different cryptocurrency wallets** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IWallet.java">IWallet</a> interface.
* **Implement support for different cryptocurrency exchanges** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExchange.java">IExchange</a> interface.
* **Implement support for different cryptocurrency rate tickers** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IRateSource.java">IRateSource</a> interface.
* **Implement support for different cryptocurrency payment processors** - ( payment processor is a company that processes payments for you. For more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IPaymentProcessor.java">IPaymentProcessor</a> interface.
* **Implement support for payments by your cryptocurrency** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/payment/IPaymentSupport.java">IPaymentSupport</a> interface.
* **Implement support for different terroris watch lists** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/watchlist/IWatchList.java">IWatchList</a> interface.
* **Implement support for different AML provider** - for more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/aml/IAMLProvider.java">IAMLProvider</a> interface.
* **Perform actions whenever transaction on server is created or updated** - This is usefull for example when in some states you need to inform tax office about the transaction in realtime and print taxoffice onetime unique id on ticket printed by ATM. For more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/ITransactionListener.java">ITransactionListener</a> interface.
* **Implement paper wallet generator for your crypto currency XYZ** - Do you want ATM to be able print paper wallet or write private key on NFC card? You need to implement this interface. For more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IPaperWalletGenerator.java">IPaperWalletGenerator</a> interface.
* **Implement fiat to fiat currency exchange rate provider** - Do you want ATM server to use fiat currency exchange rates provided by your local national bank or local exchange instead of the international market? For more see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IFiatExchangeRateProvider.java">IFiatExchangeRateProvider</a> interface.
* **Send emails or SMSes from extension** - Sometimes you want to send customer SMS or email. You can do that by calling methods on <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtensionContext.java">IExtensionContext</a> interface. 
* **<a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtensionContext.java">ExtensionContext</a>** is your main entrypoint when you want to interact with core server functionality.
ExtensionContext is possible to call from any extension. Reference to ExtensionContext is passed to an Extension when **init** method is called by server on an Extension. Please make sure you read all of the methods that are available on IExtensionContext interface. There are for example cash related operations, sell functionality and more.
* **Implement RESful serivices** - Somtimes you need to integrate server with your or 3rd party system. Extensions enable you to create quickly and easily RESTful service that receives and sends data via JSON and HTTPS. Do you want your website to contact your CAS server to find out what is the exchange rate on your terminal or do something more complcated? Use <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IRestService.java">IRestService</a> for that. Simple example returning you server version is <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/examples/rest">here</a>.

Content
=======
* **server_extensions_api** - contains extension api that all extensions use to extend BATM Server's functionality.
* **server_extensions_extra** - reference extension implementation that implements BTC, LTC, CLOAK, DGB, DASH, POT, VIA, BTX, SYS, FLASH, DOGE, NLG, ICG, NBT, GRS, MAX, BSD, MEC, BTDX, SUM, BURST, ECA, LINDA and $PAC coin support functionality.
* **server_extensions_test** - contains tester for testing the extensions without requirement of having a BATM server

Note for developers
==========

Requirements:
* Linux is required in order to run compilers and tests.
* Java
* Gradle

When you implement support for new crypto-coin add it please to **server_extensions_extra** this way it will get into default BATM Server installation pack for customers. 
For adding new functionality please use Fork and Pull Request GitHub workflow. Please have in mind that your code will be code reviewed before merge to master.

When adding new cryptocurrency support bare in mind that you need to also add its logo. This logo will be later downloaded by ATM from ATM Server and displayed on screen. SVG and PNG logos are supported however on newer version of terminals only SVG logo is used. PNG logo is present only for backward compatibility reason or for cases when SVG logo would have high byte size.
SVG logos must comply with following rules:
1. SVG logo should not contain filters or bitmap images
2. SVG and PNG logo should have predefined size 120x120px. See other logos to find out details
3. To preserve unified design on ATM's screen. Every SVG logo must contain background from <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/resources/template.svg">template.svg</a>. Example use of background you can see <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/resources/lisk.svg">here</a>.


After you implement the the extension make sure you test it with Tester which you will find in **server_extensions_test**
If you want to implement an extension that you don't want to share with the rest of the world then a create separate module for example server_extensions_mycompany and use different jar name (server_extensions_mycompany.jar). 

Build information
=================
```bash
./gradlew build
cp server_extensions_extra/build/libs/batm_server_extensions_extra.jar /batm/app/master/extensions/
```
Note that BATM server scans on start /batm/app/master/extensions/ folder for all files that have .jar extension.

How to run Tester
==========
```bash
./gradlew :server_extensions_test:install
./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test
```
