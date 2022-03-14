package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BatmExtensionsXmlTest {

    private static final String XML_FILENAME = "src/main/resources/batm-extensions.xml";

    @Test
    public void testCryptoCurrencies() throws Exception {
        for (String cryptocurrency : getXmlElementValues(XML_FILENAME, "//cryptocurrency/text()|//cryptologo/@cryptocurrency")) {
            try {
                CryptoCurrency.valueOfCode(cryptocurrency);
            } catch (IllegalArgumentException e) {
                Assert.fail(cryptocurrency + " not in " + CryptoCurrency.class.getSimpleName() + " enum");
            }
        }
    }

    @Test
    public void testCryptoLogos() throws Exception {
        for (String file : getXmlElementValues(XML_FILENAME, "//cryptologo/@file")) {
            Assert.assertTrue(file + " cryptologo file does not exist", new File("src/main/resources", file).exists());
        }
    }

    /**
     * tests that all extension class names mentioned in the XML exist
     */
    @Test
    public void testExtensionClassesExist() throws Exception {
        getExtensionInstances();
    }

    /**
     * tests that CryptoCurrencyValidator exist for all crypto currencies used in the XML
     */
    @Test
    public void testCryptoCurrencyValidators() throws Exception {
        // currencies mentioned in batm_extensions.xml but having CryptoCurrencyValidators implemented in the non-opensourced part of the codebase
        Set<String> supportedInBuiltin = ImmutableSet.of("TRTL", "BTC", "LBTC", "XMR", "ETC","EGLD","USDTTRON");

        List<IExtension> extensions = getExtensionInstances();
        getCryptoCurrencies().stream()
            .filter(cryptoCurrency -> !supportedInBuiltin.contains(cryptoCurrency))
            .filter(cryptoCurrency -> !anyExtensionProvidesCryptoCurrencyValidator(extensions, cryptoCurrency))
            .forEach(cryptoCurrency -> Assert.fail("No ICryptoAddressValidator found for " + cryptoCurrency));
    }

    private boolean anyExtensionProvidesCryptoCurrencyValidator(List<IExtension> extensions, String cryptoCurrency) {
        return extensions.stream()
            .anyMatch(extension -> extension.createAddressValidator(cryptoCurrency) != null);
    }

    private HashSet<String> getCryptoCurrencies() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        return new HashSet<>(getXmlElementValues(XML_FILENAME, "//cryptocurrency/text()"));
    }

    private List<IExtension> getExtensionInstances() throws Exception {
        List<IExtension> extensions = new ArrayList<>();
        for (String className : getXmlElementValues(XML_FILENAME, "/extensions/extension/@class")) {
            extensions.add((IExtension) Class.forName(className).newInstance());
        }
        return extensions;
    }

    private List<String> getXmlElementValues(String xmlFilename, String xpathExpression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFilename);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xpath.compile(xpathExpression);
        Object result = xPathExpression.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        List<String> res = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            res.add(nodes.item(i).getNodeValue());
        }
        return res;
    }

}