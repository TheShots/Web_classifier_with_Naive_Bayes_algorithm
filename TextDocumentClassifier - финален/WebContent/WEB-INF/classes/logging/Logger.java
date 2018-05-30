package logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import classifier.properties.PropertiesManager;

/**
 * Utility class used for logging
 */
public class Logger {
	
	private static java.util.logging.Logger logger;
	private static Handler fileHandler;
	
	private Logger() throws SecurityException, IOException {
		logger = java.util.logging.Logger.getLogger(Logger.class.getName());
		String appDir = PropertiesManager.getAppProperties().getString("appDir");
		fileHandler = new FileHandler(appDir + File.separator + "log.txt", true);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}

	/**
	 * Creates a logger instance if one does not exist yet or retrieves the already created one
	 * @return - the logger instance
	 */
	private static java.util.logging.Logger getLogger() {
		if (logger == null) {
			try {
				new Logger();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logger;
	}

	/**
	 * Logs information to a log file
	 * @param level - the level of importance of the information to be logged
	 * @param e - exception about which information should be logged
	 */
	public static void log(Level level, Exception e){
		java.util.logging.Logger logger = getLogger();
	    logger.log(level, e.getMessage(), e);
	    for (Handler handler:getLogger().getHandlers()) {
	    	handler.close();
	    }
	}
	
}
