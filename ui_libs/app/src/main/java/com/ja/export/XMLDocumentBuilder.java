package com.ja.export;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XMLDocumentBuilder {
    
    public static final Document createDocument() throws ParserConfigurationException {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document doc = builder.newDocument();
        
        return doc;
    }
    
    public static final Element addNode(Document doc, Node parent, String name, String value) {
        
        Element child = doc.createElement(name);
        
        if ( value != null ) {
            Text textNode = doc.createTextNode(value);
            child.appendChild(textNode);
            
        }
        
        if ( parent == null ) {
            doc.appendChild(child);
        } else {
            parent.appendChild(child);
        }
        return child;
    }
}
