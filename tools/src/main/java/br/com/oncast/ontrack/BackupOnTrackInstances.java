package br.com.oncast.ontrack;

import static br.com.oncast.ontrack.utils.Config.BACKUP_FILENAME_PATTERN;
import static br.com.oncast.ontrack.utils.Config.DOWNLOAD_URL_PATTERN;
import static br.com.oncast.ontrack.utils.Config.ONTRACK_INSTANCES;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;

import br.com.oncast.ontrack.utils.IOUtils;
import br.com.oncast.ontrack.utils.StringUtils;
import br.com.oncast.ontrack.utils.Workpaths;

public class BackupOnTrackInstances {
	private static final MessageFormat URL_RENDERER = new MessageFormat(DOWNLOAD_URL_PATTERN.value());
	private static final MessageFormat FILENAME_RENDERER = new MessageFormat(BACKUP_FILENAME_PATTERN.value());

	public static void main(final String[] args) {
		System.out.println("Backing up the following instances: " + StringUtils.join(ONTRACK_INSTANCES.splitValue(), ", "));

		for (final String instance : ONTRACK_INSTANCES.splitValue()) {
			final File file = new File(Workpaths.forInstance(instance), FILENAME_RENDERER.format(new Object[] { instance, new Date() }));
			System.out.println("¥ Downloading '" + instance + "' to '" + file + "'.");
			IOUtils.download(URL_RENDERER.format(new Object[] { instance }), file);
		}
	}
}
