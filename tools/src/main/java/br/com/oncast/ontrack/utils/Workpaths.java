package br.com.oncast.ontrack.utils;

import java.io.File;

public class Workpaths {
	private static final File WORK_ROOT_PATH = new File("work");

	static {
		assureDirectoryExists(WORK_ROOT_PATH);
	}

	private Workpaths() {}

	public static File forInstance(final String instance) {
		return assureDirectoryExists(new File(WORK_ROOT_PATH, instance));
	}

	private static File assureDirectoryExists(final File dir) {
		if (dir.isDirectory()) return dir;
		if (!dir.mkdirs()) throw new RuntimeException("Unable to create directory " + dir + ".");
		return dir;
	}
}
