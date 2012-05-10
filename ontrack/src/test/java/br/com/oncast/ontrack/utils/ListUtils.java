package br.com.oncast.ontrack.utils;

import java.util.List;

public class ListUtils {

	public static <T> T lastOf(final List<T> list) {
		return list.get(list.size() - 1);
	}

}
