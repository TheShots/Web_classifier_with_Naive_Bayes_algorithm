package classifier.util;

import java.util.List;
import java.util.Map;

import classifier.components.Documents;

/**
 * Manages the execution of the classification algorithm
 */
public interface DocumentClassifier {
	
	/**
	 * Performs the document classification
	 * @return - classification results;
	 *           represented as a map where the keys are the categories
	 *           which have at least one document assigned to them and the values -
	 *           a list of all documents which have been assigned to the respective category
	 */
	public Map<String, List<Documents>> classify();
	
}
