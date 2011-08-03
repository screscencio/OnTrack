package br.com.oncast.ontrack.utils.identedLogger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class IdentedLogger {

	private static final String IDENTATION_SYMBOL = "   ";
	private final List<List<String>> currentLogHierarchicalPath;
	private final PrintStream printStream;
	private int identation;

	public IdentedLogger(final PrintStream printStream) {
		this.printStream = printStream;
		currentLogHierarchicalPath = new ArrayList<List<String>>();
		currentLogHierarchicalPath.add(new ArrayList<String>());
	}

	public void log(final String message) {
		currentLogHierarchicalPath.get(identation).add(message);
		for (int i = 0; i < identation; i++)
			printStream.print(IDENTATION_SYMBOL);
		printStream.print(message);
		printStream.print('\n');
	}

	public void indent() {
		identation++;
		currentLogHierarchicalPath.add(identation, new ArrayList<String>());
	}

	public void outdent() {
		if (identation == 0) return;
		currentLogHierarchicalPath.remove(identation);
		identation--;
		currentLogHierarchicalPath.get(identation).clear();
	}

	public String getCurrentLogHierarchy() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < identation; i++) {
			String margin = "";
			for (int k = 0; k < i; k++)
				margin += IDENTATION_SYMBOL;

			final List<String> messageList = currentLogHierarchicalPath.get(i);
			for (final String message : messageList) {
				builder.append(margin);
				builder.append(message);
				builder.append('\n');
			}
		}
		return builder.toString();
	}
}
