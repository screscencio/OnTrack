package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMockWithListOfActions;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMockWithListOfActions;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.BeanMapper;

public class BeanMapperTest {

	/**
	 * Simple attribute types are String and primitive types.
	 */
	@Test
	public void shouldMapAClassWithSimpleAttributeTypes() throws Exception {
		final ModelActionMock modelAction = new ModelActionMock();
		final ModelActionEntityMock entity = (ModelActionEntityMock) BeanMapper.map(modelAction);

		assertEquals(modelAction.getReferenceId().toString(), entity.getReferenceId());
		assertEquals(modelAction.getAString(), entity.getAString());
	}

	@Test
	public void shouldMapAClassWithAListOfActions() throws Exception {
		final ModelActionMockWithListOfActions modelAction = new ModelActionMockWithListOfActions();
		modelAction.addAction(new ModelActionMockWithListOfActions());
		modelAction.addAction(new ModelActionMockWithListOfActions());
		modelAction.addAction(new ModelActionMockWithListOfActions());

		final ModelActionEntityMockWithListOfActions entity = (ModelActionEntityMockWithListOfActions) BeanMapper.map(modelAction);

		assertEquals(modelAction.getReferenceId().toString(), entity.getReferenceId());
		assertEquals(modelAction.getAString(), entity.getAString());
		assertEquals(modelAction.getAnActionList().size(), entity.getAnActionList().size());

		assertEquals(modelAction.getAnActionList().get(0).getReferenceId().toString(), entity.getAnActionList().get(0).getReferenceId());
		assertEquals(modelAction.getAnActionList().get(1).getReferenceId().toString(), entity.getAnActionList().get(1).getReferenceId());
		assertEquals(modelAction.getAnActionList().get(2).getReferenceId().toString(), entity.getAnActionList().get(2).getReferenceId());
	}

	public void shouldReturnAnModelActionFromAGivenModelActionEntity() {
		final ModelActionEntityMock entityAction = new ModelActionEntityMock();
		final ModelActionMock modelAction = (ModelActionMock) BeanMapper.map(entityAction);

		assertEquals(entityAction.getReferenceId(), modelAction.getReferenceId().toString());
		assertEquals(entityAction.getAString(), modelAction.getAString());
	}
}
