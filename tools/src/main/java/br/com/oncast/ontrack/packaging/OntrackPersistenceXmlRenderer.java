package br.com.oncast.ontrack.packaging;

import static br.com.oncast.ontrack.utils.Config.PRODUCTION_JDBC_URL_PATTERN;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;

import br.com.oncast.ontrack.utils.IOUtils;

public class OntrackPersistenceXmlRenderer {
	private static final MessageFormat JDBC_URL_RENDERER = new MessageFormat(PRODUCTION_JDBC_URL_PATTERN.value());

	private final String originalContents;

	private OntrackPersistenceXmlRenderer(final File persistenceXmlFile) {
		originalContents = IOUtils.read(persistenceXmlFile);
	}

	public static OntrackPersistenceXmlRenderer prepare(final File persistenceXmlFile) {
		return new OntrackPersistenceXmlRenderer(persistenceXmlFile);
	}

	public String render(final String instance) {
		try {
			final StringBuilder sb = new StringBuilder();
			final BufferedReader br = new BufferedReader(new StringReader(originalContents));

			boolean copying = true;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("<!-- HSQLDB -->")) {
					copying = false;
					continue;
				}
				if (line.trim().equals("<!-- /HSQLDB -->")) {
					copying = true;
					continue;
				}
				if (line.trim().equals("<!--")) continue;
				if (line.trim().equals("-->")) continue;
				if (!copying) continue;

				if (line.trim().startsWith("<property name=\"javax.persistence.jdbc.url\"")) {
					sb.append(renderJdbcUrlLine(instance)).append("\n");
					continue;
				}

				sb.append(line).append("\n");
			}

			return sb.toString();
		}
		catch (final IOException e) {
			throw new RuntimeException("Unable to render persistence.xml file for instance '" + instance + "'.", e);
		}
	}

	private String renderJdbcUrlLine(final String instance) {
		return "			<property name=\"javax.persistence.jdbc.url\" value=\""
				+ JDBC_URL_RENDERER.format(new Object[] { instance })
				+ "\" />";
	}
}
