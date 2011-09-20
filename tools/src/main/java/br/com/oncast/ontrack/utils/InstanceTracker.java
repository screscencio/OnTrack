package br.com.oncast.ontrack.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InstanceTracker {
	private static InstanceTracker INSTANCE = new InstanceTracker();
	private List<String> instances = null;

	private InstanceTracker() {}

	public static InstanceTracker get() {
		return INSTANCE;
	}

	public List<String> listInstances() {
		if (instances != null) return instances;

		instances = new ArrayList<String>();
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					InstanceTracker.class.getResourceAsStream("/br/com/oncast/ontrack/instances.txt")));
			while (true) {
				final String line = reader.readLine();
				if (line==null) break;
				if (line.trim().isEmpty()) continue;
				instances.add(line.trim());
			}
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to read instances.txt file.", e);
		}

		return instances;
	}
}
