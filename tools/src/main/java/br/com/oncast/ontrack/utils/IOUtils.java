package br.com.oncast.ontrack.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class IOUtils {

	public static void download(final String source, final File dest) {
		try {
			final URL url = new URL(source);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (HttpURLConnection.HTTP_OK != conn.getResponseCode()) { throw new RuntimeException("Http error (" + conn.getResponseCode()
					+ ") while downloading '" + source + "'."); }
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

	public static void delete(final File file) {
		if (file.isDirectory()) {
			for (final File f : file.listFiles()) {
				delete(f);
			}
		}

		if (!file.delete()) {
			System.out.println("WARN: unable to delete " + file + ".");
		}
	}

	public static void zip(final File sourceDir, final File destZip) {
		try {
			final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destZip)));
			final int canonicalPathRelativePoint = sourceDir.getCanonicalPath().length() + 1;
			zipDir(out, sourceDir, canonicalPathRelativePoint);
			out.close();
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to zip directory ('" + sourceDir + "') into zip file ('" + destZip + "').", e);
		}
	}

	private static void zipDir(final ZipOutputStream out, final File dir, final int canonicalPathRelativePoint) throws IOException {
		for (final File f : dir.listFiles()) {
			if (f.isDirectory()) {
				zipDir(out, f, canonicalPathRelativePoint);
				continue;
			}

			final BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f), 2048);
			final ZipEntry entry = new ZipEntry(f.getCanonicalPath().substring(canonicalPathRelativePoint).replace('\\', '/'));
			out.putNextEntry(entry);

			int count;
			final byte data[] = new byte[2048];
			while ((count = origin.read(data, 0, 2048)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
		}
	}

	public static void unzip(final File zipFile, final File destDir) {
		try {
			final ZipFile zipfile = new ZipFile(zipFile);
			final Enumeration<?> e = zipfile.entries();
			while (e.hasMoreElements()) {
				final ZipEntry entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) continue;

				final BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry));
				final File destFile = new File(destDir, entry.getName());
				assureDirectoryExists(destFile.getParentFile());
				final BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(destFile), 2048);
				int count;
				final byte data[] = new byte[2048];
				while ((count = is.read(data, 0, 2048)) != -1) {
					dest.write(data, 0, count);
				}
				dest.close();
				is.close();
			}
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to unzip '" + zipFile + "'.", e);
		}
	}

	public static File assureDirectoryExists(final File dir) {
		if (dir.isDirectory()) return dir;
		if (!dir.mkdirs()) throw new RuntimeException("Unable to create directory " + dir + ".");
		return dir;
	}

	public static String read(final File file) {
		try {
			final StringBuilder sb = new StringBuilder();
			final BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to read file '" + file + "'.", e);
		}
	}

	public static void write(final String contents, final File file) {
		try {
			final FileOutputStream fos = new FileOutputStream(file);
			fos.write(contents.getBytes());
			fos.close();
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to write into file '" + file + "'.", e);
		}
	}
}
