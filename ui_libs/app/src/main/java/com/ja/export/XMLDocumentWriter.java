package com.ja.export;

import java.io.File;

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

//    public static final void writeDocument(String outputFilename, Document doc) throws TransformerException, IOException {

        // setup the xml transformer
        // this will convert our xml to a string that we can print out
//        TransformerFactory transfac = TransformerFactory.newInstance();
//        Transformer trans = transfac.newTransformer();
//        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
//        StringWriter sw = new StringWriter();
//        StreamResult result = new StreamResult(sw);
//        DOMSource source = new DOMSource(doc);
//        trans.transform(source, result);
//        String xmlString = sw.toString();
//
//        // get the external sdcard location
//        File directory = getSDCardLocation();
//        if ( directory != null ) {
//            File write = new File(directory, outputFilename);
//            BufferedWriter output = new BufferedWriter(new FileWriter(write));
//            output.write(xmlString);
//            output.close();
//        } else {
//            // TODO notification of some sort :|
//
//        }
//    }
}
