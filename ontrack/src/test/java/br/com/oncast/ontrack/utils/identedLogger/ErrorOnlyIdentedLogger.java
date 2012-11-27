package br.com.oncast.ontrack.utils.identedLogger;

import java.util.ArrayList;
import java.util.List;

public class ErrorOnlyIdentedLogger implements IdentedLogger {

	private static final String IDENTATION_SYMBOL = "   ";
	private final List<List<String>> currentLogHierarchicalPath;
	private int identation;

	public ErrorOnlyIdentedLogger() {
		currentLogHierarchicalPath = new ArrayList<List<String>>();
		currentLogHierarchicalPath.add(new ArrayList<String>());
	}

	@Override
	public void log(final String message) {
		currentLogHierarchicalPath.get(identation).add(message);
	}

	@Override
	public void indent() {
		identation++;
		currentLogHierarchicalPath.add(identation, new ArrayList<String>());
	}

	@Override
	public void outdent() {
		if (identation == 0) return;
		currentLogHierarchicalPath.remove(identation);
		identation--;
		currentLogHierarchicalPath.get(identation).clear();
	}

	@Override
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
