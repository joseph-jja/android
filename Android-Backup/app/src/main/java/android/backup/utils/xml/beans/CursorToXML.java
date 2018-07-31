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
package android.backup.utils.xml.beans;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class CursorToXML {

    private Cursor cur;
    private String filename;
    private String topLevelNode;
    private String sectionName;
    private Map<String, String> columnNames;
    private boolean useAttributes;
    
    public CursorToXML() {
        
        this.columnNames = new HashMap<String, String>();
        this.useAttributes = false;
    }
    
    /**
     * @return the cur
     */
    public Cursor getCur() {
        return cur;
    }
    /**
     * @param cur the cur to set
     */
    public void setCur(Cursor cur) {
        this.cur = cur;
    }
    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    /**
     * @return the topLevelNode
     */
    public String getTopLevelNode() {
        return topLevelNode;
    }
    /**
     * @param topLevelNode the topLevelNode to set
     */
    public void setTopLevelNode(String topLevelNode) {
        this.topLevelNode = topLevelNode;
    }
    /**
     * @return the sectionName
     */
    public String getSectionName() {
        return sectionName;
    }
    /**
     * @param sectionName the sectionName to set
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    /**
     * @return the columnNames
     */
    public Map<String, String> getColumnNames() {
        return columnNames;
    }
    /**
     * @param columnNames the columnNames to set
     */
    public void setColumnNames(Map<String, String> columnNames) {
        this.columnNames = columnNames;
    }
    /**
     * @return the useAttributes
     */
    public boolean isUseAttributes() {
        return useAttributes;
    }
    /**
     * @param useAttributes the useAttributes to set
     */
    public void setUseAttributes(boolean useAttributes) {
        this.useAttributes = useAttributes;
    }
}
