package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.BeanConverter;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionEntityMockWithListOfActions;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMock;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks.ModelActionMockWithListOfActions;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class BeanConverterTest {

	/**
	 * Simple attribute types are String and primitive types.
	 */
	@Test
	public void shouldMapAClassWithSimpleAttributeTypes() throws Exception {
		final ModelActionMock modelAction = new ModelActionMock();
		final ModelActionEntityMock entity = (ModelActionEntityMock) new BeanConverter().convert(modelAction);

		assertEquals(modelAction.getAString(), entity.getAString());
	}

	@Test
	public void shouldConvertAnInteger() throws BeanConverterException {
		final int original = 1;
		final Object converted = new BeanConverter().convert(original);

		assertEquals(original, converted);
	}

	@Test
	public void shouldConvertAnUUID() throws BeanConverterException {
		final UUID original = new UUID();
		final Object converted = new BeanConverter().convert(original);

		assertEquals(original.toStringRepresentation(), converted);
	}

	@Test
	public void shouldConvertAnBoolean() throws BeanConverterException {
		final boolean original = true;
		final Object converted = new BeanConverter().convert(original);

		assertEquals(original, converted);
	}

	@Test
	public void shouldMapAClassWithAListOfActions() throws Exception {
		final ModelActionMockWithListOfActions modelAction = new ModelActionMockWithListOfActions();
		modelAction.addAction(new ModelActionMockWithListOfActions());
		modelAction.addAction(new ModelActionMockWithListOfActions());
		modelAction.addAction(new ModelActionMockWithListOfActions());

		final ModelActionEntityMockWithListOfActions entity = (ModelActionEntityMockWithListOfActions) new BeanConverter().convert(modelAction);

		assertEquals(modelAction.getAnActionList().size(), entity.getAnActionList().size());

		assertEquals(modelAction.getAnActionList().get(0).getAString(), entity.getAnActionList().get(0).getAString());
		assertEquals(modelAction.getAnActionList().get(1).getAString(), entity.getAnActionList().get(1).getAString());
		assertEquals(modelAction.getAnActionList().get(2).getAString(), entity.getAnActionList().get(2).getAString());
	}

}
