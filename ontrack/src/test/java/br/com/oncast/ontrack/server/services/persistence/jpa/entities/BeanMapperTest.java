package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMockWithListOfActions;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMockWithListOfString;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMockWithListOfActions;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMockWithListOfString;

public class BeanMapperTest {

	/**
	 * Simple attribute types are String and primitive types.
	 */
	@Test
	public void shouldMapAClassWithSimpleAttributesType() throws Exception {
		final ModelActionMock modelAction = new ModelActionMock();
		final ModelActionEntityMock entity = (ModelActionEntityMock) BeanMapper.map(modelAction);

		assertEquals(modelAction.getReferenceId().toString(), entity.getReferenceId());
		assertEquals(modelAction.getAString(), entity.getAString());
	}

	@Test
	public void shouldMapAClassWithAListOfString() throws Exception {
		final ModelActionMockWithListOfString modelAction = new ModelActionMockWithListOfString();
		final ModelActionEntityMockWithListOfString entity = (ModelActionEntityMockWithListOfString) BeanMapper.map(modelAction);

		assertEquals(modelAction.getReferenceId().toString(), entity.getReferenceId());
		assertEquals(modelAction.getAString(), entity.getAString());
		assertEquals(modelAction.getaStringList(), entity.getaStringList());
	}

	@Test
	public void shouldMapAClassWithAListOfActions() throws Exception {
		final ModelActionMockWithListOfActions modelAction = new ModelActionMockWithListOfActions();
		final ModelActionEntityMockWithListOfActions entity = (ModelActionEntityMockWithListOfActions) BeanMapper.map(modelAction);

		assertEquals(modelAction.getReferenceId().toString(), entity.getReferenceId());
		assertEquals(modelAction.getAString(), entity.getAString());
		assertEquals(modelAction.getAnActionList(), entity.getAnActionList());
	}
}
