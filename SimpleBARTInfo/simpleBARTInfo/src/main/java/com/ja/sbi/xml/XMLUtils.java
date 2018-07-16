package

public class XMLUtils {

   public String append(String initialString, String xmlData) { 
		
		if ( initialString != null && initialString.length() > 0 ) {
			return initialString + xmlData;
		}
		return xmlData;		
	}
  
}
