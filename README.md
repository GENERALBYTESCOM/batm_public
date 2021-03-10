BATM Public Repository - Open Extensions
===========

This repository contains Bitcoin ATM related code used in BATMTwo, BATMThree, BATM Server (CAS) products.

More information about the products can be found here: https://www.generalbytes.com


All source code is released under GPL2.

Architecture
========
![Architecture](https://raw.githubusercontent.com/GENERALBYTESCOM/batm_public/master/doc/open_extensions.png)



Overview
========
Operators frequently request new features to be added to our BATM Server (CAS). Typically, the requests are for adding support for "crypto currency XYZ" or "digital asset exchange XY".

The purpose of this project is to give operators the desired power to add features themselves by **extending** CAS via this published **Extensions** mechanism.

Our software is written in Java and any Java developer may be recruited to create/implement these extensions. No unusual qualifications are required.

When the CAS services are started, they scan the <code>/batm/app/master/extensions/</code> folder for all files that have the ".jar" extension.
CAS then parses any "batm-extensions.xml" found in the JAR file to enumerate the extensions present within the JAR package.

<a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtension.java">Extension</a> is a high level component - something like a plugin - that encapsulates and instantiates the rest of the features.

Extension can be asked to provide wallet X for currency Y etc. The best way to learn more about extensions is to read the sample code and examine how other people have implemented support for their wallet or cryptocurrency in the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra">server_extensions_extra</a> module. <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/examples">Additional examples can be found here</a>.

Here is the list of some functionality that can be extended using Extensions API:
* **Implement support for different cryptocurrency wallets** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IWallet.java">IWallet</a> interface.
* **Implement support for different cryptocurrency exchanges** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExchange.java">IExchange</a> interface.
* **Implement support for different cryptocurrency rate tickers** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IRateSource.java">IRateSource</a> interface.
* **Implement support for different cryptocurrency payment processors** - ( payment processor is a company that processes payments for you. For more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IPaymentProcessor.java">IPaymentProcessor</a> interface.
* **Implement support for payments by your cryptocurrency** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/payment/IPaymentSupport.java">IPaymentSupport</a> interface.
* **Implement support for different terrorist watch lists** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/watchlist/IWatchList.java">IWatchList</a> interface.
* **Implement support for a different AML provider** - for more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/aml/IAMLProvider.java">IAMLProvider</a> interface.
* **Perform actions whenever a transaction on CAS is created or updated** - This is useful; for example, in those locales where you may need to notify the tax office about the transaction in realtime and print a unique tax office onetime id on the ticket printed by the BATM. For more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/ITransactionListener.java">ITransactionListener</a> interface.
* **Implement a paper wallet generator for your cryptocurrency XYZ** - Do you want your BATM to be able to print a paper wallet or write the transaction's private key on an NFC card? You will need to implement this interface. For more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IPaperWalletGenerator.java">IPaperWalletGenerator</a> interface.
* **Implement a fiat-to-fiat currency exchange rate provider** - Do you want CAS to use the fiat currency exchange rates provided by your local bank (or exchange) instead of the international market? For more information, see the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IFiatExchangeRateProvider.java">IFiatExchangeRateProvider</a> interface.
* **Send emails or SMSes from extension** - To notify your customer via SMS or email with custom messages, call the methods exposed by the <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtensionContext.java">IExtensionContext</a> interface.
* **<a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IExtensionContext.java">ExtensionContext</a>** is your main entrypoint for interacting with CAS.
ExtensionContext may be called from any extension. A reference to ExtensionContext is passed to an Extension when the **init** method is called by CAS on any Extension. Please make sure you read all of the methods that are available on the IExtensionContext interface. There are, for example: cash related operations, sell functionality, and more!
* **Implement RESTful services** - facilitates integration of the Server with a 3rd party system. Extensions enable you to quickly and easily create a RESTful service that sends/receives data via JSON and HTTPS. Do you want your website to contact  CAS to find the current exchange rate on your BATM (or even more complicated functions)? Use <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_api/src/main/java/com/generalbytes/batm/server/extensions/IRestService.java">IRestService</a> for that. A simple example that returns your current CAS version can be found <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/examples/rest">here</a>.
* **Implement ChatBot commands** - Do you need to execute some tasks on server by sending message to server via Telegram Messenger? Simply implement Telegram your command and you are ready to go. A simple example that returns your current CAS version can be found <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/java/com/generalbytes/batm/server/extensions/extra/examples/chat">here</a>.


Content
=======
* **server_extensions_api** - contains the extension API that all extensions use to extend CAS' functionality.
* **server_extensions_extra** - reference extension implementation that demonstrates BTC, LTC, CLOAK, DGB, DASH, HATCH, POT, VIA, BTX, SYS, FLASH, DOGE, NLG, ICG, NBT, GRS, MAX, BSD, MEC, BTDX, NANO, SUM, BURST, ECA, LINDA, $PAC, DAI, MKR, MUE, BAT and REP coin support functionality.
* **server_extensions_test** - contains tester for testing the extensions (CAS not required).

Note for developers
==========

Requirements:
* Linux is required in order to run compilers and tests.
* Java **1.8** (we recommend using https://sdkman.io/ for managing multiple JDK versions on your computer)
* Gradle

When you implement support for a new crypto-coin, please add it to **server_extensions_extra** - so that it may get into the default CAS installation pack for customers.
Please use the appropriate Fork and Pull Request in the GitHub workflow when adding new functions, and bear in mind that your code will be reviewed prior to any merge with the master.

When adding new cryptocurrency support, please remember to provide its logo! This logo will later be downloaded by the BATM from CAS and displayed on the BATM screen. Both SVG and PNG logos are supported; however, only the SVG logo is used on newer BATM versions. A PNG logo is offered only for backward compatibility, and in the few cases where the SVG logo has an unusually large size.
SVG logos must comply with following rules:
1. SVG logos should not contain filters or bitmap images
2. All SVG and PNG logos should be 120 x 120px. See other logos for examples.
3. Preserve the unified BATM design: every SVG logo must contain the background from <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/resources/template.svg">template.svg</a>. An example background can be found <a href="https://github.com/GENERALBYTESCOM/batm_public/blob/master/server_extensions_extra/src/main/resources/lisk.svg">here</a>.


After you implement the extension make sure you test it with "Tester" (which you will find in **server_extensions_test**).
If you want to implement an extension that you don't want to share with the rest of the world, then create a separate module (e.g. server_extensions_mycompany) and use a different jar name (server_extensions_mycompany.jar).

Build information
=================
```bash
./gradlew build
cp server_extensions_extra/build/libs/batm_server_extensions_extra.jar /batm/app/master/extensions/
```
Note that on startup, CAS scans the: <code>/batm/app/master/extensions/</code> folder for all files that have the .jar extension.

If you happen to add new crypto currency in CryptoCurrency class in currencies module then don't forget to make sure that you copy your version of currencies-1.0.XX-SNAPSHOT.jar to /batm/app/master/lib

How to run Tester
==========
```bash
./gradlew :server_extensions_test:install
./server_extensions_test/build/install/server_extensions_test/bin/server_extensions_test
```
