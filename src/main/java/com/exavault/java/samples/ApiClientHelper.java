package com.exavault.java.samples;


import com.exavault.client.ApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Helper class to get {@link com.exavault.client.ApiClient} reference
 * <p>
 * To obtain your API Key and Token, you'll need to use the Developer page within the web file manager
 * <p>
 * See <a href="https://www.exavault.com/developer/api-docs/#section/Obtaining-Your-API-Key-and-Access-Token">getApiKey</a>
 * <p>
 * Access tokens do not expire, so you should only need to obtain the key and token once.
 * <p>
 */
public class ApiClientHelper {

	private static final Logger LOGGER = Logger.getLogger(ApiClientHelper.class.getName());
	//Constants used for API Key and Token Properties
	private static final String CREDENTIALS = "credentials.properties";
	private static final String EXAVAULT_API_KEY = "exavault.api.Key";
	private static final String EXAVAULT_ACCESS_TOKEN = "exavault.access.token";
	private static final String EXAVAULT_API_URL = "exavault.api.url";
	private static ApiClient instance;
	private static Credential credential;

	private ApiClientHelper() {
	}

	/**
	 * Loads the credentials from the properties file, and create an {@code ApiClient} object
	 *
	 * @return the newly constructed {@code ApiClient}
	 */
	public static ApiClient initApiClient() {
		if (instance == null) {
			instance = new ApiClient();
			initCredential();
			if (credential != null) {
				instance.setBasePath(credential.getApiUrl());
			}
		}
		return instance;
	}

	/**
	 * Parse the properties file and load them into {@code Credential} Object
	 */
	private static void initCredential() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try (InputStream input = loader.getResourceAsStream(CREDENTIALS)) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			credential = new Credential(prop.getProperty(EXAVAULT_API_KEY),
				prop.getProperty(EXAVAULT_ACCESS_TOKEN), prop.getProperty(EXAVAULT_API_URL));
		} catch (IOException ex) {
			LOGGER.severe("Could not load properties file");
			throw new IllegalStateException("Credentials could not be read");
		}
	}

	public static Credential getCredential() {
		return credential;
	}
}
