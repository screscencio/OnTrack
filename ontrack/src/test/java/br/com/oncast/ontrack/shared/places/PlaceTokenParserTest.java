package br.com.oncast.ontrack.shared.places;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class PlaceTokenParserTest {

	@Test
	public void shouldNotHaveProjectWhenAnEmptyStringIsGiven() {
		final PlaceTokenParser parser = new PlaceTokenParser("");
		assertNull(parser.get(PlaceTokenType.PROJECT));
		assertFalse(parser.has(PlaceTokenType.PROJECT));
	}

	@Test
	public void shouldGetTheProject() {
		final UUID projectId = new UUID();
		final PlaceTokenParser parser = new PlaceTokenParser(projectId.toString());
		assertEquals(projectId, parser.get(PlaceTokenType.PROJECT));
		assertTrue(parser.has(PlaceTokenType.PROJECT));
	}

	@Test
	public void shouldNotHaveScopeWhenOnlyProjectStringIsGiven() throws Exception {
		final PlaceTokenParser parser = new PlaceTokenParser(new UUID().toString());
		assertNull(parser.get(PlaceTokenType.SCOPE));
		assertFalse(parser.has(PlaceTokenType.SCOPE));
	}

	@Test
	public void shouldBeAbleToGetScope() throws Exception {
		final UUID projectId = new UUID();
		final UUID scopeId = new UUID();
		final PlaceTokenParser parser = new PlaceTokenParser(projectId.toString() + PlaceTokenType.SCOPE.getIdentifier() + scopeId.toString());
		assertEquals(projectId, parser.get(PlaceTokenType.PROJECT));
		assertTrue(parser.has(PlaceTokenType.PROJECT));
		assertEquals(scopeId, parser.get(PlaceTokenType.SCOPE));
		assertTrue(parser.has(PlaceTokenType.SCOPE));
	}

}
