package br.com.oncast.ontrack.packaging;

import java.io.File;

import br.com.oncast.ontrack.utils.IOUtils;
import br.com.oncast.ontrack.utils.Workpaths;

public class OntrackWarPackage {
	private File warDir;

	private OntrackWarPackage(final File originalWarPackage) {
		warDir = Workpaths.temp();
		IOUtils.unzip(originalWarPackage, warDir);
	}

	public static OntrackWarPackage prepare(final File file) {
		return new OntrackWarPackage(file);
	}

	public void setPersistenceXml(final String contents) {
		final File persistenceXmlFile = new File(warDir, "WEB-INF/classes/META-INF/persistence.xml");
		IOUtils.write(contents, persistenceXmlFile);
	}

	public void output(final File destWar) {
		IOUtils.zip(warDir, destWar);
	}

	public void cleanup() {
		IOUtils.delete(warDir);
	}
}
