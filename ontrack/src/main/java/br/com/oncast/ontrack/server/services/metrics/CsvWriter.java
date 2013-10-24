package br.com.oncast.ontrack.server.services.metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvWriter {

	private static final char CSV_TEXT_DELIMITER = '"';

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd");

	private static final char CSV_ITEM_SEPARATOR = ',';

	private static final String CSV_ENTRY_SEPARATOR = "\r\n";

	private final OutputStream out;

	public CsvWriter(final OutputStream out, final String... header) throws IOException {
		this.out = out;
		for (int i = 0; i < header.length - 1; i++) {
			write(header[i]);
			and();
		}
		write(header[header.length - 1]);
		closeEntry();
	}

	public CsvWriter closeEntry() throws IOException {
		out.write(CSV_ENTRY_SEPARATOR.getBytes());
		return this;
	}

	public CsvWriter and() throws IOException {
		out.write(CSV_ITEM_SEPARATOR);
		return this;
	}

	public CsvWriter write(final Date date) throws IOException {
		out.write((date == null ? "" : DATE_FORMATTER.format(date)).getBytes());
		return this;
	}

	public CsvWriter write(final String text) throws IOException {
		out.write(CSV_TEXT_DELIMITER);
		out.write(escapeDoubleQuotes(text).getBytes());
		out.write(CSV_TEXT_DELIMITER);
		return this;
	}

	private String escapeDoubleQuotes(final String text) {
		return text.replaceAll("\"", "\"\"");
	}

}
