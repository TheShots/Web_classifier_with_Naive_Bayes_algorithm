package request.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zeroturnaround.zip.ZipUtil;

import com.datumbox.opensource.classifiers.NaiveBayes;
import com.datumbox.opensource.dataobjects.NaiveBayesKnowledgeBase;
import com.datumbox.opensource.examples.NaiveBayesExample;

import classifier.components.Documents;
import classifier.properties.PropertiesManager;

/**
 * Manages HTTP requests for classification
 */
public class ClassifierServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Parses all request parameters, launches the classification process, and prepares the results
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String appDir = PropertiesManager.getAppProperties().getString("appDir");
		String docsDirName = "classified_docs_" + request.getSession().getAttribute("stringID");
		Path docsDir = Paths.get(appDir, docsDirName);

		// Read all supplied documents and collect them in a list
		List<Documents> listDocuments = new LinkedList<Documents>();
		Files.list(docsDir)
		.forEach(d -> listDocuments.add(new Documents(d.toString())));

		// Collect all categories and keywords data from the request parameters,
		// create Category instances and collect them in a list
		List<String> categories = new LinkedList<>();
			categories.add("English");
			categories.add("French");
			categories.add("German");


		// Create a classifier instance and perform the classification
		NaiveBayesExample nbExample = new NaiveBayesExample();
		NaiveBayesKnowledgeBase knowledgeBase = nbExample.getKnowledgeBase();
		NaiveBayes nb = new NaiveBayes(knowledgeBase, listDocuments);
        Map<String, Set<Documents>> results = nb.predict();

		// For each category in the results map create a separate folder
		// and move all documents to the folder of their respective category
		for (String category:results.keySet()) {
			Path categoryDir = Paths.get(docsDir.toString(), category);
			if (!Files.exists(categoryDir))
			Files.createDirectory(categoryDir);
			for (final Documents doc:results.get(category)) {
				Path oldFileLocation = Paths.get(docsDir.toAbsolutePath().toString(), doc.getName());
				Path newFileLocation = Paths.get(categoryDir.toAbsolutePath().toString(), doc.getName());
				Files.move(oldFileLocation, newFileLocation);
			}
		}

		// Put all category folders and their contents in a ZIP archive
		Path archive = Paths.get(appDir, docsDirName + ".zip");
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + "Koreni.zip");
		ZipUtil.pack(docsDir.toFile(), archive.toFile());
		response.setContentLength((int)archive.toFile().length());

		// Send the ZIP with the classification results as a response
		ServletOutputStream op = response.getOutputStream();
		byte[] arBytes = new byte[32768];
		try (
		  FileInputStream is = new FileInputStream(archive.toFile())){
		  int count;
		  while ((count = is.read(arBytes)) > 0)
		  {
		      op.write(arBytes, 0, count);
		  }
		  op.flush();
		}

	}

}