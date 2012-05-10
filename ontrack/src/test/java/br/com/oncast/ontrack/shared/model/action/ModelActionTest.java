package br.com.oncast.ontrack.shared.model.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.persistence.Entity;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

public abstract class ModelActionTest {

	@Test
	public void actionShouldHaveConvertToAnnotation() throws Exception {
		assertHasAnnotation(getActionType(), ConvertTo.class);
	}

	@Test
	public void actionShouldConvertToTheRightEntity() throws Exception {
		assertConvertTo(getActionType(), getEntityType());
	}

	@Test
	public void actionShouldHaveAConstructorWithNoArguments() throws Exception {
		try {
			getActionType().getDeclaredConstructor();
		}
		catch (final NoSuchMethodException e) {
			fail(getActionName() + " should have a constructor with no arguments.");
		}
	}

	@Test
	public void actionShouldNotHaveNonStaticFinalFields() throws Exception {
		assertDontHaveNonStaticFinalFields(getActionType());
	}

	@Test
	public void actionsNonStaticFieldsShouldHaveMatchingConversionAlias() throws Exception {
		assertAllNonStaticFieldsHaveMatchingConversionAlias(getActionType(), getEntityType());
	}

	@Test
	public void entityShouldHaveEntityAnnotation() throws Exception {
		assertHasAnnotation(getEntityType(), Entity.class);
	}

	@Test
	public void entitysNameAttributeShouldBeTheActionNameWithountTheActionPostfix() throws Exception {
		assertEquals(getActionName().replaceAll("Action$", ""), getEntityType().getAnnotation(Entity.class).name());
	}

	@Test
	public void entityShouldHaveConvertToAnnotation() throws Exception {
		assertHasAnnotation(getEntityType(), ConvertTo.class);
	}

	@Test
	public void entityShouldConvertToTheRightAction() throws Exception {
		assertConvertTo(getEntityType(), getActionType());
	}

	@Test
	public void entityShouldNotHaveNonStaticFinalFields() throws Exception {
		assertDontHaveNonStaticFinalFields(getEntityType());
	}

	@Test
	public void entitysNonStaticFieldsShouldHaveMatchingConversionAlias() throws Exception {
		assertAllNonStaticFieldsHaveMatchingConversionAlias(getEntityType(), getActionType());
	}

	private void assertHasAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
		assertNotNull(type.getSimpleName() + " should be annotated with " + annotation.getSimpleName() + ".",
				type.getAnnotation(annotation));
	}

	private void assertConvertTo(final Class<?> source, final Class<?> target) {
		final ConvertTo annotation = source.getAnnotation(ConvertTo.class);
		assertEquals("ConvertTo annotation should have " + target.getSimpleName() + " as value.",
				target,
				annotation.value());
	}

	private void assertDontHaveNonStaticFinalFields(final Class<?> type) {
		for (final Field field : type.getDeclaredFields()) {
			final int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers)) continue;

			assertFalse("The non static field '" + field.getName() + "' can't be final.",
					Modifier.isFinal(modifiers));
		}
	}

	private void assertAllNonStaticFieldsHaveMatchingConversionAlias(final Class<?> source, final Class<?> target) {
		for (final Field field : source.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) continue;

			final String aliasName = getAliasName(field);

			assertNotNull("The field with name '" + aliasName + "' or with @ConversionAlias('" + aliasName + "') was not found on " + target.getSimpleName(),
					getMatchingAliasField(aliasName, target));
		}
	}

	private Field getMatchingAliasField(final String aliasName, final Class<?> target) {
		for (final Field targetField : target.getDeclaredFields()) {
			if (aliasName.equals(getAliasName(targetField))) return targetField;
		}
		return null;
	}

	private String getAliasName(final Field field) {
		final ConversionAlias alias = field.getAnnotation(ConversionAlias.class);
		return alias == null ? field.getName() : alias.value();
	}

	private String getActionName() {
		return getActionType().getSimpleName();
	}

	protected abstract Class<? extends ModelActionEntity> getEntityType();

	protected abstract Class<? extends ModelAction> getActionType();
}
