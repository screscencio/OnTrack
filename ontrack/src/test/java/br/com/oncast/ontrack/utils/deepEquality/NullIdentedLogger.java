package br.com.oncast.ontrack.utils.deepEquality;

import br.com.oncast.ontrack.utils.identedLogger.IdentedLoggerImpl;

public class NullIdentedLogger extends IdentedLoggerImpl {

	public NullIdentedLogger() {
		super(null);
	}

	@Override
	public void indent() {}

	@Override
	public void log(final String message) {}

	@Override
	public String getCurrentLogHierarchy() {
		return "";
	}

	@Override
	public void outdent() {}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof NullIdentedLogger;
	}

}
