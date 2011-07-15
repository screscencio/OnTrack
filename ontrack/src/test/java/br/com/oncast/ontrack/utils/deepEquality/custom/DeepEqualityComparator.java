package br.com.oncast.ontrack.utils.deepEquality.custom;

import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityException;

public interface DeepEqualityComparator<T> {

	public void assertObjectEquality(final T expected, final T actual) throws DeepEqualityException;

}
