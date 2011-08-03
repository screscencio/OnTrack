package br.com.oncast.ontrack.utils.identedLogger;

import java.io.PrintStream;

public class IdentedLogger {

	private final PrintStream printStream;
	private int identation;

	public IdentedLogger(final PrintStream printStream) {
		this.printStream = printStream;
	}

	public void log(final String message) {
		for (int i = 0; i < identation; i++)
			printStream.print("  ");
		printStream.println(message);
	}

	public void indent() {
		identation++;
	}

	public void outdent() {
		if (identation == 0) return;
		identation--;
	}

}
