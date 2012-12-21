package br.com.oncast.ontrack.server.services.persistence.cache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ObjectCache<T, U> {

	private final Map<T, WeakReference<U>> map = new HashMap<T, WeakReference<U>>();

	public U get(final T key) {
		if (!map.containsKey(key)) return null;
		return map.get(key).get();
	}

	public void set(final T key, final U object) {
		map.put(key, new WeakReference<U>(object));
	}

}
