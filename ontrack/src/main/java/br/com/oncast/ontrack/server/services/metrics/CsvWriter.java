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

	private static final String EMPTY_ITEM = "";

	private final OutputStream out;

	private int itemsCounter;

	private final int size;

	private boolean needsToBeClosed;

	public CsvWriter(final OutputStream out, final String... header) throws IOException {
		this.out = out;
		size = header.length - 1;
		for (int i = 0; i < size; i++) {
			write(header[i]);
			and();
		}
		write(header[size]);
		closeEntry();
	}

	public CsvWriter closeEntry() throws IOException {
		if (itemsCounter == 0 && !needsToBeClosed) return this;
		for (int i = itemsCounter; i < size; i++) {
			and().writeEmpty();
		}
		out.write(CSV_ENTRY_SEPARATOR.getBytes());
		itemsCounter = 0;
		needsToBeClosed = false;
		return this;
	}

	public CsvWriter and() throws IOException {
		if (itemsCounter++ < size) out.write(CSV_ITEM_SEPARATOR);
		else closeEntry();
		needsToBeClosed = false;
		return this;
	}

	public CsvWriter writeEmpty() throws IOException {
		doWrite(EMPTY_ITEM);
		return this;
	}

	public CsvWriter write(final Date date) throws IOException {
		doWrite(date == null ? EMPTY_ITEM : DATE_FORMATTER.format(date));
		return this;
	}

	public CsvWriter write(final String text) throws IOException {
		if (text == null) writeEmpty();
		else {
			doWrite(CSV_TEXT_DELIMITER + escapeDoubleQuotes(text) + CSV_TEXT_DELIMITER);
		}
		return this;
	}

	private void doWrite(final String value) throws IOException {
		if (needsToBeClosed) and();
		out.write(value.getBytes());
		needsToBeClosed = true;
	}

	private String escapeDoubleQuotes(final String text) {
		return text.replaceAll("\"", "\"\"");
	}

}
