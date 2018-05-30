package request.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import classifier.properties.PropertiesManager;
import logging.Logger;

/**
 * Manages HTTP requests, concerning data about documents
 */
public class DocsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Uploads files to the server
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		Path resultsDir = createUploadDirectory(request);
		if (resultsDir != null) {	
			uploadFiles(request, resultsDir);
		}
	}

	/**
	 * Deletes files from the server
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
		Path resultsDir = getUploadDirectory(request.getSession());
		Map<String, String[]> docsToRemove = request.getParameterMap();
		for (String[] docName:docsToRemove.values()) {
			Path doc = Paths.get(resultsDir.toString(), docName[0]);
			try {
				Files.delete(doc);
			} catch (IOException ioe) {
				Logger.log(Level.SEVERE, ioe);
			}
		}
	}

	/**
	 * Retrieves the directory where the documents which were uploaded during a particular session
	 * are located
	 * @param session - the session
	 * @return
	 */
	protected static Path getUploadDirectory(HttpSession session) {
		String appDir = PropertiesManager.getAppProperties().getString("appDir");
		String resultsDirName = "classified_docs_" + session.getAttribute("stringID");
		Path resultsDir = Paths.get(appDir, resultsDirName);
		return resultsDir;
	}

	/**
	 * Creates a directory where to store all documents which were uploaded during a particular session
	 * @param request
	 * @return
	 */
	private static Path createUploadDirectory(HttpServletRequest request) {
		Path resultsDir = getUploadDirectory(request.getSession());
		if (!Files.exists(resultsDir)) {
			try {
				resultsDir = Files.createDirectory(resultsDir);
			} catch (IOException ioe) {
				Logger.log(Level.SEVERE, ioe);
				return null;
			}
		}
		return resultsDir;
	}

	/**
	 * Uploads files, specified in a HTTP request, to a server 
	 * @param request - the request
	 * @param uploadLocation - the location on the server where the files should be uploaded
	 */
	private static void uploadFiles(HttpServletRequest request, Path uploadLocation) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> formItems = upload.parseRequest(request);
            for (FileItem formItem:formItems) {
                if (!formItem.isFormField()) {
                	String fileName = formItem.getName();
    			    File outFile = new File(uploadLocation + File.separator + fileName);
    			    try (
    			        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
    			        InputStream fileContents = new BufferedInputStream(formItem.getInputStream())) {
    			        int read = 0;
    			        byte[] bytes = new byte[1024];
    			        while ((read = fileContents.read(bytes)) != -1) {
    			            out.write(bytes, 0, read);
    			        }
    			    } catch (FileNotFoundException fnfe) {
    			    	Logger.log(Level.SEVERE, fnfe);
    			    }
                }
            }
        } catch (FileUploadException | IOException e) {
        	Logger.log(Level.SEVERE, e);
        }
	}

}
