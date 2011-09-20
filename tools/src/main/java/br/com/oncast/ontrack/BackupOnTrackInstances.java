package br.com.oncast.ontrack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.oncast.ontrack.utils.InstanceTracker;
import br.com.oncast.ontrack.utils.Workpaths;

public class BackupOnTrackInstances {
	private static final SimpleDateFormat DATE_TAG = new SimpleDateFormat("yyyy-MM-dd-hhmm");

	public static void main(final String[] args) {
		System.out.print("Backing up the following instances:");
		for (final String instance : InstanceTracker.get().listInstances()) {
			System.out.print(" " + instance);
		}
		System.out.println();

		for (final String instance : InstanceTracker.get().listInstances()) {
			System.out.println("¥ Downloading '" + instance + "' to '" + Workpaths.forInstance(instance) + "'.");
			downloadInstanceMM(instance);
		}
	}

	private static void downloadInstanceMM(final String instance) {
		try {
			final URL url = new URL("http://192.168.2.95:8888/" + instance + "/application/mindmap/download");
			final InputStream is = new BufferedInputStream(url.openStream());
			final OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(Workpaths.forInstance(instance), renderBackupMMFileName(instance))));
			for (int data = is.read();data!=-1;data=is.read()) {
				os.write(data);
			}

			os.close();
			is.close();
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to download instance '" + instance + "'.", e);
		}
	}

	private static String renderBackupMMFileName(final String instance) {
		return instance + " " + DATE_TAG.format(new Date()) + ".mm";
	}
}
