package br.com.oncast.ontrack.server.configuration;

import java.io.IOException;
import java.util.Properties;

public class Configurations {

	private static Configurations instance = null;
	private final Properties properties = new Properties();

	private Configurations() {
		try {
			properties.load(Configurations.class.getResourceAsStream("/environment.properties"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Configurations getInstance() {
		if (instance == null) instance = new Configurations();
		return instance;
	}

	public String getEmailUsername() {
		return properties.getProperty("email.user");
	}

	public String getEmailPassword() {
		return properties.getProperty("email.password");
	}

	// FIXME change it to GWT.getModuleBaseURL() or GWT.getHostPageBaseURL();
	public String getApplicationBaseUrl() {
		return properties.getProperty("application.base_url");
	}

	public String getStorageBaseDir() {
		return properties.getProperty("storage.base_dir");
	}

	public long getMaxFileSizeInBytes() {
		final String value = properties.getProperty("storage.file_max_upload_size");
		return value == null ? -1 : Long.valueOf(value);
	}
}
