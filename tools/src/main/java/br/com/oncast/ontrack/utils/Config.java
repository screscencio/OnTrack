package br.com.oncast.ontrack.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public enum Config {
	DOWNLOAD_URL_PATTERN("download.url.pattern"),
	BACKUP_FILENAME_PATTERN("backup.filename.pattern"),
	ONTRACK_INSTANCES("ontrack.instances");

	private static final Properties PROPERTIES = new Properties();

	static {
		try {
			PROPERTIES.load(Config.class.getResourceAsStream("/config.properties"));
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to load properties file.",e);
		}
	}

	private final String propertyName;

	private Config(final String propertyName) {
		this.propertyName = propertyName;
	}

	public String value() {
		final String value = PROPERTIES.getProperty(propertyName);
		if (value==null) throw new RuntimeException("No value defined for property '" + propertyName + "'.");
		return value;
	}

	public List<String> splitValue() {
		final List<String> values = new ArrayList<String>();
		for (final String v:value().split(",")) {
			if (v.trim().isEmpty()) continue;
			values.add(v.trim());
		}

		return values;
	}
}
