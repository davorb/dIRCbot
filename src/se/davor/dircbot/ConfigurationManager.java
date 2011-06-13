package se.davor.dircbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

public class ConfigurationManager {
	Properties properties;

	ConfigurationManager(String fileName) throws IOException {
		properties = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		properties.load(fis);
		fis.close();
	}

	ConfigurationManager() throws IOException {
		this("settings.conf");
	}

	public String getKey(String key) throws NoSuchElementException {
		if (properties.containsKey(key))
			return properties.getProperty(key);
		else
			throw new NoSuchElementException();
	}
	
	/**
	 * Checks that the key is true. If it is, returns
	 * true. Otherwise returns false. If the key doesn't
	 * exist, returns false.
	 */
	public boolean isTrue(String key) {
		try {
			boolean b = new Boolean(getKey(key));
			return b;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}
