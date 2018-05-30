package request.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.io.FileUtils;

import logging.Logger;

/**
 * Utility class for managing sessions and tasks related to them
 */
public class SessionListener implements HttpSessionListener {

	/**
	 * Creates a unique string ID for a session when one is created;
	 * used to distinguish sessions from one another,
	 * so that their resources (e.g. uploaded documents) can be managed adequately
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		setRequestSessionID(session);
	}

	/**
	 * Deletes all documents which were uploaded or created during the session when the session expires;
	 * used to remove redundant resources from the server
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		try {
			Path sessionDocsDir = DocsServlet.getUploadDirectory(event.getSession());
			FileUtils.deleteDirectory(sessionDocsDir.toFile());
			Path sessionDocsZIP = Paths.get(sessionDocsDir.toString() + ".zip");
			if (Files.exists(sessionDocsZIP)) {
				Files.delete(sessionDocsZIP);
			}
		} catch (IOException ioe) {
			Logger.log(Level.SEVERE, ioe);
		}
	}

	/**
	 * Sets a unique string ID for a session if it does not have one already
	 * @param session - the session
	 */
	private static void setRequestSessionID(HttpSession session) {
		if (session.getAttribute("stringID") == null) {
			session.setAttribute("stringID", generateRandomAlphanumericString(16));
		}
	}

	/**
	 * Generates a random alphanumeric string;
	 * used for creating unique string IDs
	 * @param size - the size of the string to be generated
	 * @return - the random alphanumeric string
	 */
	private static String generateRandomAlphanumericString(int size) {
		String charsStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(size);
	    for (int i = 0; i < size; i++) {
	        sb.append(charsStr.charAt(random.nextInt(charsStr.length())));
	    }
	    return sb.toString();
	}

}