package br.com.oncast.ontrack.server.utils.introspector;

public interface Introspector<T> {

	void introspect(T object) throws Exception;
}
