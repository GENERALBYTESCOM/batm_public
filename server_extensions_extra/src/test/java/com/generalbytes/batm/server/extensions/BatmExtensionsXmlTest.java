package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
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
import java.util.List;

public class BatmExtensionsXmlTest {
    @Test
    public void testCryptoCurrencies() throws Exception {
        for (String cryptocurrency : getXmlElementValues("src/main/resources/batm-extensions.xml", "//cryptocurrency/text()|//cryptologo/@cryptocurrency")) {
            try {
                CryptoCurrency.valueOfCode(cryptocurrency);
            } catch (IllegalArgumentException e) {
                Assert.fail(cryptocurrency + " not in " + CryptoCurrency.class.getSimpleName() + " enum");
            }
        }
    }

    @Test
    public void testCryptoLogos() throws Exception {
        for (String file : getXmlElementValues("src/main/resources/batm-extensions.xml", "//cryptologo/@file")) {
            Assert.assertTrue(file + " cryptologo file does not exist", new File("src/main/resources", file).exists());
        }
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