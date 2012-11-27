package br.com.oncast.ontrack.utils.identedLogger;

public interface IdentedLogger {

	public abstract void log(String message);

	public abstract void indent();

	public abstract void outdent();

	public abstract String getCurrentLogHierarchy();

}