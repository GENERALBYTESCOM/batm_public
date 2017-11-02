/*************************************************************************************
 * Copyright (C) 2015 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.test;

import com.generalbytes.batm.server.extensions.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tester {
    private List<IExtension> extensions;
    private Document descriptors;

    public static void main(String[] args) {
        Tester t = new Tester();
        t.go(args);
    }

    private static void usage(){
        System.out.println("Welcome to BATM Extensions Tester");
        System.out.println("Usage:");
        System.out.println();
        System.out.println("tester [OPTIONS] -a action -j file.jar");
        System.out.println();
        System.out.println(" OPTIONS:");
        System.out.println(" -a action");
        System.out.println("   Specifies which action should be performed.");
        System.out.println(" -j file.jar");
        System.out.println("   Specifies path to a jar file which contains extensions.");
        System.out.println(" -n name");
        System.out.println("   Specifies name of wallet/ratesource/exchnage/pprocessor to be used");
        System.out.println(" -p=param1:param2:param3");
        System.out.println("   Set parameters to wallet/ratesource/exchnage/pprocessor.");

        System.out.println(" ACTIONS:");
        System.out.println("  COMMON ACTIONS:");
        System.out.println("   list-ratesources");
        System.out.println("     Lists available rate sources from extensions in jar.");
        System.out.println("   list-wallets");
        System.out.println("     Lists available wallets from extensions in jar.");
        System.out.println("   list-exchanges");
        System.out.println("     Lists available exchanges from extensions in jar.");
        System.out.println("   list-paymentprocessors");
        System.out.println("     Lists available payment processors from extensions in jar.");
        System.out.println();
        System.out.println("  RATE SOURCES ACTIONS:");
        System.out.println("   get-rates");
        System.out.println("     Instantiates a rate source and retrieves current rates.");
        System.out.println();
        System.out.println("  WALLET ACTIONS:");
        System.out.println("   get-wbalance");
        System.out.println("     Instantiates a wallet and retrieves current balance.");
        System.out.println();
        System.out.println("  EXCHANGE ACTIONS:");
        System.out.println("   get-ebalance");
        System.out.println("     Instantiates a exchange and retrieves current balance.");
        System.out.println();

    }

    public void go(String[] args) {
        OptionParser parser = new OptionParser( "a:j:n:p:" );

        OptionSet options = parser.parse( args );

        if (args.length ==0) {
            usage();
            System.exit(1);
        }

        if (!options.has("j")) {
            System.err.println("Error: Missing -j argument.");
            usage();
            System.exit(1);
        }

        File file = new File((String) options.valueOf("j"));
        if (!file.exists()) {
            System.err.println("Error: Couldn't find extensions jar file.");
            usage();
            System.exit(1);
        }

        if (!options.has("a")) {
            System.err.println("Error: Missing action argument.");
            usage();
            System.exit(1);
        }
        loadExtensions(file);
        if (extensions == null || extensions.isEmpty()) {
            System.err.println("Error: None of the extensions was loaded.");
            usage();
            System.exit(1);
        }else{
            for (IExtension extension : extensions) {
                System.out.println("Loaded extension: " + extension.getName());
            }
        }

        String action = (String) options.valueOf("a");
        if ("list-ratesources".equalsIgnoreCase(action)) {
            listRateSources();
        }else if ("list-wallets".equalsIgnoreCase(action)) {
            listWallets();
        }else if ("list-exchanges".equalsIgnoreCase(action)) {
            listExchanges();
        }else if ("list-paymentprocessors".equalsIgnoreCase(action)) {
            listPaymentProcessors();
        }else if ("get-rates".equalsIgnoreCase(action)) {
            if (!options.hasArgument("n")) {
                System.err.println("Error: Missing -n parameter.");
                usage();
                System.exit(1);
            }
            final String name = (String) options.valueOf("n");
            final String params = (String) options.valueOf("p");
            getRates(name,params);
        }else if ("get-wbalance".equalsIgnoreCase(action)) {
            if (!options.hasArgument("n")) {
                System.err.println("Error: Missing -n parameter.");
                usage();
                System.exit(1);
            }
            if (!options.hasArgument("p")) {
                System.err.println("Error: Missing -p parameter.");
                usage();
                System.exit(1);
            }
            final String name = (String) options.valueOf("n");
            final String params = (String) options.valueOf("p");
            getWalletBalance(name, params);
        }else if ("get-ebalance".equalsIgnoreCase(action)) {
            if (!options.hasArgument("n")) {
                System.err.println("Error: Missing -n parameter.");
                usage();
                System.exit(1);
            }
            if (!options.hasArgument("p")) {
                System.err.println("Error: Missing -p parameter.");
                usage();
                System.exit(1);
            }
            final String name = (String) options.valueOf("n");
            final String params = (String) options.valueOf("p");
            getExchangeBalance(name, params);
        }else{
            System.err.println("Error: Unknown Action.");
            usage();
            System.exit(1);
        }
        System.exit(0);
    }

    private void loadExtensions(File extensionsFile) {
        try {
            URL url = extensionsFile.toURL();
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);
            InputStream resourceAsStream = cl.getResourceAsStream("batm-extensions.xml");
            if (resourceAsStream != null) {
               loadExtensionsFromFile(resourceAsStream, cl);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loadExtensionsFromFile(InputStream resourceAsStream, ClassLoader classLoader) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            descriptors = builder.parse(resourceAsStream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        if (descriptors != null) {
            //load extensions
            final NodeList childNodes = descriptors.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if ("extension".equalsIgnoreCase(node.getNodeName())) {
                    try {
                        String clazz = node.getAttributes().getNamedItem("class").getTextContent();
                        Class<?> aClass = classLoader.loadClass(clazz);
                        IExtension extension = (IExtension) aClass.newInstance();
                        if (extensions == null) {
                            extensions = new ArrayList<IExtension>();
                        }
                        extensions.add(extension);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void listRateSources() {
        final NodeList childNodes = descriptors.getDocumentElement().getChildNodes();
        System.out.println("Rate Sources:");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("extension".equals(node.getNodeName())) {
                final NodeList items = node.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    if ("ratesource".equals(item.getNodeName())) {
                        String prefix = item.getAttributes().getNamedItem("prefix").getTextContent();
                        String name = item.getAttributes().getNamedItem("name").getTextContent();
                        String params = "";
                        String cryptoCurrencies = "";
                        final NodeList specs = item.getChildNodes();
                        for (int k = 0; k < specs.getLength(); k++) {
                            Node spec = specs.item(k);
                            if ("cryptocurrency".equals(spec.getNodeName())) {
                                cryptoCurrencies += spec.getTextContent() +",";
                            }else if ("param".equals(spec.getNodeName())) {
                                String pname = spec.getAttributes().getNamedItem("name").getTextContent();
                                params+=pname+":";
                            }
                        }
                        if (!params.isEmpty()) {
                            params = " -p=" + params.substring(0,params.length() -1);
                        }
                        if (!cryptoCurrencies.isEmpty()) {
                            cryptoCurrencies = cryptoCurrencies.substring(0,cryptoCurrencies.length() -1);
                        }
                        System.out.println(" -n=" + prefix + " - " + name + "(" + cryptoCurrencies + ") " + params);
                    }
                }
            }
        }
    }

    private void listWallets() {
        final NodeList childNodes = descriptors.getDocumentElement().getChildNodes();
        System.out.println("Wallets:");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("extension".equals(node.getNodeName())) {
                final NodeList items = node.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    if ("wallet".equals(item.getNodeName())) {
                        String prefix = item.getAttributes().getNamedItem("prefix").getTextContent();
                        String name = item.getAttributes().getNamedItem("name").getTextContent();
                        String params = "";
                        String cryptoCurrencies = "";
                        final NodeList specs = item.getChildNodes();
                        for (int k = 0; k < specs.getLength(); k++) {
                            Node spec = specs.item(k);
                            if ("cryptocurrency".equals(spec.getNodeName())) {
                                cryptoCurrencies += spec.getTextContent() +",";
                            }else if ("param".equals(spec.getNodeName())) {
                                String pname = spec.getAttributes().getNamedItem("name").getTextContent();
                                params+=pname+":";
                            }
                        }
                        if (!params.isEmpty()) {
                            params = " -p=" + params.substring(0,params.length() -1);
                        }
                        if (!cryptoCurrencies.isEmpty()) {
                            cryptoCurrencies = cryptoCurrencies.substring(0,cryptoCurrencies.length() -1);
                        }
                        System.out.println(" -n=" + prefix + " - " + name + "(" + cryptoCurrencies + ") " + params);
                    }
                }
            }
        }
    }

    private void listExchanges() {
        final NodeList childNodes = descriptors.getDocumentElement().getChildNodes();
        System.out.println("Exchanges:");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("extension".equals(node.getNodeName())) {
                final NodeList items = node.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    if ("exchange".equals(item.getNodeName())) {
                        String prefix = item.getAttributes().getNamedItem("prefix").getTextContent();
                        String name = item.getAttributes().getNamedItem("name").getTextContent();
                        String params = "";
                        String cryptoCurrencies = "";
                        final NodeList specs = item.getChildNodes();
                        for (int k = 0; k < specs.getLength(); k++) {
                            Node spec = specs.item(k);
                            if ("cryptocurrency".equals(spec.getNodeName())) {
                                cryptoCurrencies += spec.getTextContent() +",";
                            }else if ("param".equals(spec.getNodeName())) {
                                String pname = spec.getAttributes().getNamedItem("name").getTextContent();
                                params+=pname+":";
                            }
                        }
                        if (!params.isEmpty()) {
                            params = " -p=" + params.substring(0,params.length() -1);
                        }
                        if (!cryptoCurrencies.isEmpty()) {
                            cryptoCurrencies = cryptoCurrencies.substring(0,cryptoCurrencies.length() -1);
                        }
                        System.out.println(" -n=" + prefix + " - " + name + "(" + cryptoCurrencies + ") " + params);
                    }
                }
            }
        }
    }

    private void listPaymentProcessors() {
        final NodeList childNodes = descriptors.getDocumentElement().getChildNodes();
        System.out.println("Payment Processors:");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("extension".equals(node.getNodeName())) {
                final NodeList items = node.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    Node item = items.item(j);
                    if ("paymentprocessor".equals(item.getNodeName())) {
                        String prefix = item.getAttributes().getNamedItem("prefix").getTextContent();
                        String name = item.getAttributes().getNamedItem("name").getTextContent();
                        String params = "";
                        String cryptoCurrencies = "";
                        final NodeList specs = item.getChildNodes();
                        for (int k = 0; k < specs.getLength(); k++) {
                            Node spec = specs.item(k);
                            if ("cryptocurrency".equals(spec.getNodeName())) {
                                cryptoCurrencies += spec.getTextContent() +",";
                            }else if ("param".equals(spec.getNodeName())) {
                                String pname = spec.getAttributes().getNamedItem("name").getTextContent();
                                params+=pname+":";
                            }
                        }
                        if (!params.isEmpty()) {
                            params = " -p=" + params.substring(0,params.length() -1);
                        }
                        if (!cryptoCurrencies.isEmpty()) {
                            cryptoCurrencies = cryptoCurrencies.substring(0,cryptoCurrencies.length() -1);
                        }
                        System.out.println(" -n=" + prefix + " - " + name + "(" + cryptoCurrencies + ") " + params);
                    }
                }
            }
        }
    }





    private void getRates(String name, String params) {
        for (int i = 0; i < extensions.size(); i++) {
            IExtension extension = extensions.get(i);
            final IRateSource rs = extension.createRateSource(name + ":" + params);
            if (rs != null) {
                final String preferredFiatCurrency = rs.getPreferredFiatCurrency();
                final Set<String> fiatCurrencies = rs.getFiatCurrencies();
                final Set<String> cryptoCurrencies = rs.getCryptoCurrencies();

                System.out.println("Preferred Fiat Currency = " + preferredFiatCurrency);
                System.out.println("Fiat Currencies:");
                for (String fiatCurrency : fiatCurrencies) {
                    System.out.println("  " + fiatCurrency);
                }
                System.out.println("Crypto Currencies:");
                String selectedCryptoCurrency = null;
                for (String cryptoCurrency : cryptoCurrencies) {
                    if (selectedCryptoCurrency == null) {
                        selectedCryptoCurrency = cryptoCurrency;
                    }
                    System.out.println("  " + cryptoCurrency);
                }
                final BigDecimal exchangeRateLast = rs.getExchangeRateLast(selectedCryptoCurrency, preferredFiatCurrency);
                if (exchangeRateLast != null) {
                    System.out.println("Exchange Rate Last: 1 " + selectedCryptoCurrency + " = " + exchangeRateLast.stripTrailingZeros().toPlainString() + " " + preferredFiatCurrency);
                }else{
                    System.err.println("Rate source returned NULL.");
                }

                if (rs instanceof IRateSourceAdvanced) {
                    IRateSourceAdvanced rsa = (IRateSourceAdvanced)rs;

                    for (String fiatCurrency : fiatCurrencies) {
                        System.out.println("Checking price for " + fiatCurrency);

                        final BigDecimal buyPrice = rsa.getExchangeRateForBuy(selectedCryptoCurrency, fiatCurrency);
                        if (buyPrice != null) {
                            System.out.println("Buy Price: 1 " + selectedCryptoCurrency + " = " + buyPrice.stripTrailingZeros().toPlainString() + " " + fiatCurrency);
                        }else{
                            System.err.println("Rate source returned NULL on Buy Price.");
                        }

                        final BigDecimal sellPrice = rsa.getExchangeRateForSell(selectedCryptoCurrency, fiatCurrency);
                        if (sellPrice != null) {
                            System.out.println("Sell Price: 1 " + selectedCryptoCurrency + " = " + sellPrice.stripTrailingZeros().toPlainString() + " " + fiatCurrency);
                        }else{
                            System.err.println("Rate source returned NULL on Sell Price.");
                        }

                    }

                }
                return;
            }
        }
        System.err.println("Error: Rate Source not found.");
    }

    private void getWalletBalance(String name, String params) {
        for (int i = 0; i < extensions.size(); i++) {
            IExtension extension = extensions.get(i);
            final IWallet w = extension.createWallet(name + ":" + params);
            if (w != null) {
                final String preferredCryptoCurrency = w.getPreferredCryptoCurrency();
                final Set<String> cryptoCurrencies = w.getCryptoCurrencies();

                System.out.println("Preferred Crypto Currency = " + preferredCryptoCurrency);
                System.out.println("Crypto Currencies:");
                for (String cryptoCurrency : cryptoCurrencies) {
                    System.out.println("  " + cryptoCurrency);
                }

                final String cryptoAddress = w.getCryptoAddress(preferredCryptoCurrency);
                System.out.println("CryptoAddress = " + cryptoAddress);
                final BigDecimal balance = w.getCryptoBalance(preferredCryptoCurrency);
                if (balance != null) {
                    System.out.println("Balance: " + balance.stripTrailingZeros().toPlainString() + " " + preferredCryptoCurrency);
                }else{
                    System.err.println("Wallet returned NULL.");
                }

                return;
            }
        }
        System.err.println("Error: Wallet not found.");
    }

    private void getExchangeBalance(String name, String params) {
        for (int i = 0; i < extensions.size(); i++) {
            IExtension extension = extensions.get(i);
            final IExchange e = extension.createExchange(name + ":" + params);
            if (e != null) {
                final String preferredFiatCurrency = e.getPreferredFiatCurrency();
                final Set<String> cryptoCurrencies = e.getCryptoCurrencies();
                final Set<String> fiatCurrencies = e.getFiatCurrencies();

                System.out.println("Preferred Fiat Currency = " + preferredFiatCurrency);
                System.out.println("Crypto Currencies:");
                String selectedCryptoCurrency = null;
                for (String cryptoCurrency : cryptoCurrencies) {
                    if (selectedCryptoCurrency == null) {
                        selectedCryptoCurrency = cryptoCurrency;
                    }
                    System.out.println("  " + cryptoCurrency);
                }
                System.out.println("Fiat Currencies:");
                for (String fiatCurrency : fiatCurrencies) {
                    System.out.println("  " + fiatCurrency);
                }
                final BigDecimal balance = e.getCryptoBalance(selectedCryptoCurrency);
                if (balance != null) {
                    System.out.println("Crypto Balance: " + balance.stripTrailingZeros().toPlainString() + " " + selectedCryptoCurrency);
                }else{
                    System.err.println("Exchange returned NULL.");
                }
                final String depositAddress = e.getDepositAddress(selectedCryptoCurrency);
                System.out.println("Deposit Address: " + depositAddress);
                return;
            }
        }
        System.err.println("Error: Exchange not found.");
    }


}
