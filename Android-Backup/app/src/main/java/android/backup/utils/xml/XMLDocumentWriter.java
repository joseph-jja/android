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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.os.Environment;

public class XMLDocumentWriter {

    public static final File getSDCardLocation() {

        File directory = null;
        
        File sdcard = Environment.getExternalStorageDirectory();
        if ( sdcard != null && sdcard.isDirectory() && sdcard.canWrite() ) {
            directory = new File(sdcard + System.getProperty("file.separator") + "AndroidBackup");
            boolean exists = directory.exists();
            if ( ! exists ) {
                exists = directory.mkdirs();
            }
        } 
        return directory;
    }

    public static final void writeDocument(String outputFilename, Document doc) throws TransformerException, IOException {

        // setup the xml transformer 
        // this will convert our xml to a string that we can print out
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        String xmlString = sw.toString();

        // get the external sdcard location 
        File directory = getSDCardLocation();
        if ( directory != null ) {
            File write = new File(directory, outputFilename);
            BufferedWriter output = new BufferedWriter(new FileWriter(write), xmlString.length()+1);
            output.write(xmlString);
            output.close();
        } else {
            // TODO notification of some sort :|

        }
    }
}
