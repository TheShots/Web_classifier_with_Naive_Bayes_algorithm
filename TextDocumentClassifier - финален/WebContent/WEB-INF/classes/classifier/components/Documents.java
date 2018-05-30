package classifier.components;

import java.nio.file.Paths;
import java.util.Map;

/**
 * Represents a document to be classified
 */
public class Documents {

	private String location;
    private Map<String, Integer> tokens;
    private String category;

    public Documents() {
		this.location = null;
		this.tokens = null;
		this.category = null;
	}

	public Documents(final String location) {
		this.location = location;
		this.tokens = null;
		this.category = null;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String docPath) {
		this.location = docPath;
	}

	public Map<String, Integer> getTokens() {
		return tokens;
	}

	public void setTokens(final Map<String, Integer> tokens) {
		this.tokens = tokens;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	/**
	 * Retrieves the name of the document (with its extension)
	 * @return - the name of the document
	 */
	public String getName() {
		String name = "";

		if (null != location)
		{
			name = Paths.get(location).getFileName().toString();
		}

		return name;
	}

}
