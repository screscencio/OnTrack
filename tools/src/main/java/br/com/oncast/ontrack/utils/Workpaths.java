package br.com.oncast.ontrack.utils;

import java.io.File;

public class Workpaths {
	private static final File WORK_ROOT_PATH = new File("work");

	static {
		IOUtils.assureDirectoryExists(WORK_ROOT_PATH);
	}

	private Workpaths() {}

	public static File forInstance(final String instance) {
		return IOUtils.assureDirectoryExists(new File(WORK_ROOT_PATH, instance));
	}

	public static File wars() {
		return IOUtils.assureDirectoryExists(new File(WORK_ROOT_PATH, "--wars--"));
	}

	public static File temp() {
		return IOUtils.assureDirectoryExists(new File(WORK_ROOT_PATH, "--temp-" + System.currentTimeMillis() + "--"));
	}
}
