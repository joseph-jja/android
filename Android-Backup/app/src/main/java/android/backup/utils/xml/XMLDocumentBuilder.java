/**
 * Copyright 2012 Joseph Acosta
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package android.backup.utils.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XMLDocumentBuilder {
    
    public static final Document createDocument() throws ParserConfigurationException {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document doc = builder.newDocument();
        
        return doc;
    }
    
    public static final Element addNode(Document doc, Element parent, String name, String value) {
        
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

    public static final void addAttribute(Document doc, Element parent, String attrName, String attrValue) {
        
        Attr child = doc.createAttribute(attrName); 
        
        if ( attrValue != null ) {
            int blen = attrValue.length();
            StringBuilder bytesOut = new StringBuilder();
            for ( int i = 0; i < blen; i+=1 ) { 
                char b = attrValue.charAt(i);
                if ( b >= 32 ) {
                    bytesOut.append(b);
                }
            }
            child.setValue(bytesOut.toString());
        }
        
        if ( parent != null ) {     
            parent.setAttributeNode(child);
        }
    }
}