package br.com.oncast.ontrack.server.util.introspector;

public interface Introspector<T> {

	void introspect(T object) throws Exception;
}
