package br.com.oncast.ontrack.server.utils.typeConverter.custom;

import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;
import br.com.oncast.ontrack.utils.model.FileRepresentationTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("rawtypes")
public class CollectionToListConverterTest {

	@Test
	public void shouldConvertAEmptyArrayListToAnotherEmptyArrayList() throws Exception {
		final List collection = new ArrayList<String>();
		final List convertedList = convertTo(collection, ArrayList.class);
		assertTrue(convertedList.isEmpty());
	}

	@Test
	public void shouldConvertAEmptyArrayListToAEmptyLinkedList() throws Exception {
		final List collection = new ArrayList<String>();
		final List convertedList = convertTo(collection, LinkedList.class);

		assertTrue(convertedList.isEmpty());
	}

	@Test
	public void shouldConverAEmptyHashSetToAEmptyArrayList() throws Exception {
		final Set collection = new HashSet<String>();
		final List convertedList = convertTo(collection, ArrayList.class);

		assertTrue(convertedList.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldConvertTheCollectionsSimpleContentAswell() throws Exception {
		final Collection<String> collection = new HashSet<String>();
		collection.add("first String element");
		collection.add("second String element");

		final List convertedList = convertTo(collection, ArrayList.class);

		AssertTestUtils.assertCollectionEquality(collection, convertedList);
	}

	@Test
	public void shouldConvertTheCollectionsConverWithAnnotatedContentAswell() throws Exception {
		final Collection<FileRepresentation> collection = new HashSet<FileRepresentation>();
		collection.add(FileRepresentationTestUtils.create());
		collection.add(FileRepresentationTestUtils.create());

		final List convertedList = convertTo(collection, ArrayList.class);

		assertEquals(collection.size(), convertedList.size());

		for (final Object convertedObject : convertedList) {
			assertTrue(collection.contains(new GeneralTypeConverter().convert(convertedObject)));
		}
	}

	private <T extends List> List convertTo(final Collection collection, final Class<T> clazz) throws TypeConverterException {
		final Object convertedObject = new CollectionToListConverter<T>(clazz).convert(collection);

		assertEquals(clazz, convertedObject.getClass());
		return (List) convertedObject;
	}

}
