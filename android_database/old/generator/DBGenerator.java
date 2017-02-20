package com.ja.generator;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DBGenerator {

  public static void main(String args[]) throws Exception {

    DBGenerator dbgen = new DBGenerator();
    if ( args.length > 0 ) {
    	String filenames = args[0];
        if ( new File(filenames).isDirectory() ) {
        	String names[] = new File(filenames).list();
        	for ( String fname : names ) {
        		// FIXME check for trailing slash and add if missing 
        		dbgen.generate(filenames + fname);
        	}
        } else if ( new File(filenames).isFile() ) { 
    	    dbgen.generate(filenames);
        }
    }
  }

  public void generate(String xmlTableIn) throws Exception {

    // sax parsing
    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    SAXParser parser = saxFactory.newSAXParser();

    DBGenerator.XMLHandler handler = new DBGenerator.XMLHandler();

    // parser
    parser.parse(xmlTableIn, handler);
    
    // now we have the table info data
    DBGeneratorTable table = handler.getDBGeneratorTable();

    // now we generate the table output
    DBGeneratorTableWriter tableWriter = new DBGeneratorTableWriter(table);
    tableWriter.generate();

    DBGeneratorTableManagerWriter factoryWriter = new DBGeneratorTableManagerWriter(table);
    factoryWriter.generate();

  }

  /**
   * inner class for the sax handler to generate DBBaseTable,
   * DBTableFactory and subclasses
   *
   * @author Joseph Acosta (jacosta)
   * @date Mar 1, 2010
   *
   */
  private static class XMLHandler extends DefaultHandler {

    private String currentElement = "";
    private DBGeneratorTable table;

    public XMLHandler() {
        this.table = new DBGeneratorTable();
    }

    public DBGeneratorTable getDBGeneratorTable() {
        return this.table;
    }

    public void  startElement(String uri, String localName,
        String elementName, Attributes attributes) throws SAXException

    {

      if ( elementName.equals("table") && attributes != null && attributes.getLength() > 0 ) {
        this.currentElement = "table";
        this.table.setTableName(attributes.getValue("name"));
        this.table.setPkgName(attributes.getValue("package"));
      } else if ( elementName.equals("column") && attributes != null && attributes.getLength() > 0 ) {
        DBGeneratorColumn column = new DBGeneratorColumn();
        column.setName((String)attributes.getValue("name"));
        column.setType((String)attributes.getValue("type"));
        if ( attributes.getValue("isnull") != null && attributes.getValue("isnull").equalsIgnoreCase("true") ) {
          column.setNullable(true);
        } else {
          column.setNullable(false);
        }
        column.setVersion(Integer.parseInt(attributes.getValue("version")));
        this.table.addColumn(column);
      }
    }

   /*
    private String getElementContent(String oldContent, String newContent) {

      // no old conent
      if ( StringUtils.isEmpty(oldContent) ) {
        return newContent;
      }
      return oldContent + newContent;
    }
    public void characters(char[] ch, int start, int length) throws SAXException
    {
      // what fields do we want
      String content = new String(ch,start,length);
      if (this.currentElement.equals("BLURB")) {
        getElementContent(null,  content));
      }
    }
   */

    public void endElement(String uri, String localName,String qName) throws SAXException
    {
      if ( this.currentElement.equals("") && qName.equals("table") ) {
        // TODO close file for output
      }
      this.currentElement = "";
    }
  }
}
