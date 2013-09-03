package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("rawtypes")
public class ListToHashSetConverterTest {

	@Test
	public void shouldConvertTheListToAHashSetWithConvertedContent() throws Exception {
		final Set<String> collection = new HashSet<String>();
		collection.add("string 1");
		collection.add("string 2");

		final HashSet<String> hashSetConvertedTwice = convertAndConvertBack(collection);

		assertTrue(collection.containsAll(hashSetConvertedTwice));
	}

	@Test
	public void shouldConverEmptyList() throws Exception {
		final Set<String> collection = new HashSet<String>();

		final HashSet<String> hashSetConvertedTwice = convertAndConvertBack(collection);

		AssertTestUtils.assertCollectionEquality(collection, hashSetConvertedTwice);

	}

	@SuppressWarnings({ "unchecked" })
	private <T> HashSet<T> convertAndConvertBack(final Set<T> collection) throws TypeConverterException {
		final List convertedList = (List) new CollectionToListConverter<ArrayList>(ArrayList.class).convert(collection);
		final Object convertedBackObject = new ListToHashSetConverter().convert(convertedList);

		assertTrue(convertedBackObject instanceof HashSet);

		final HashSet convertedTwiceHashSet = (HashSet) convertedBackObject;
		return convertedTwiceHashSet;
	}

}
