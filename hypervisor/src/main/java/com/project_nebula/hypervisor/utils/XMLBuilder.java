package com.project_nebula.hypervisor.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLBuilder {

    private final Document document;
    private Element currentElement;

    public XMLBuilder(String root) throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
        currentElement = document.createElement(root);
        document.appendChild(currentElement);
    }

    public XMLBuilder addChild(String name) {
        Element child = document.createElement(name);
        currentElement.appendChild(child);
        currentElement = child;
        return this;
    }

    public XMLBuilder setAttribute(String name, String value) {
        currentElement.setAttribute(name, value);
        return this;
    }

    public XMLBuilder setText(String text) {
        currentElement.appendChild(document.createTextNode(text));
        return this;
    }

    public XMLBuilder stepBack(int by) throws NullPointerException {
        for (int i = 0; i < by; i++) {
            currentElement = (Element) currentElement.getParentNode();
        }
        return this;
    }

    public String build() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }

}
