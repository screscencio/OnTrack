package br.com.oncast.ontrack.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IOUtils {

	public static void download(final String source, final File dest) {
		try {
			final URL url = new URL(source);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (HttpURLConnection.HTTP_OK!= conn.getResponseCode()) {
				throw new RuntimeException("Http error (" + conn.getResponseCode() + ") while downloading '" + source + "'.");
			}
			final InputStream is = new BufferedInputStream(conn.getInputStream());
			final OutputStream os = new BufferedOutputStream(new FileOutputStream(dest));
			for (int data = is.read(); data != -1; data = is.read()) {
				os.write(data);
			}

			os.close();
			is.close();
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to download '" + source + "'.", e);
		}
	}

}
