package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.utils.FileRepresentationTestUtils;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;

public class ListToHashSetConverterTest {

	@Test
	public void shouldConvertTheListToAHashSetWithConvertedContent() throws Exception {
		final Set<FileRepresentation> collection = new HashSet<FileRepresentation>();
		collection.add(FileRepresentationTestUtils.create());
		collection.add(FileRepresentationTestUtils.create());

		final HashSet<FileRepresentation> hashSetConvertedTwice = convertAndConvertBack(collection);

		assertTrue(collection.containsAll(hashSetConvertedTwice));
	}

	@Test
	public void shouldConverEmptyList() throws Exception {
		final Set<FileRepresentation> collection = new HashSet<FileRepresentation>();

		final HashSet<FileRepresentation> hashSetConvertedTwice = convertAndConvertBack(collection);

		AssertTestUtils.assertCollectionEquality(collection, hashSetConvertedTwice);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> HashSet<T> convertAndConvertBack(final Set<T> collection) throws TypeConverterException {
		final List convertedList = (List) new CollectionToListConverter<ArrayList>(ArrayList.class).convert(collection);
		final Object convertedBackObject = new ListToHashSetConverter().convert(convertedList);

		assertTrue(convertedBackObject instanceof HashSet);

		final HashSet convertedTwiceHashSet = (HashSet) convertedBackObject;
		return convertedTwiceHashSet;
	}

}
