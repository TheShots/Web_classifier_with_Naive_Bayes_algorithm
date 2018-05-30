package classifier.properties;

import java.util.ResourceBundle;

/**
 * Utility class for managing application properties
 */
public class PropertiesManager {
	
	/**
	 * Retrieves all properties from the application's properties file
	 * @return - resource bundle with all properties from the application's properties file
	 */
	public static ResourceBundle getAppProperties() {
		return ResourceBundle.getBundle("classifier.properties.TextDocumentClassifier");
	}
	
}
