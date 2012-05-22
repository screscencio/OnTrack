package br.com.oncast.ontrack.shared.model.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.utils.actions.ModelActionEntityFieldAnnotationsTestUtils;

import com.google.gwt.dev.util.collect.HashSet;

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
	public void actionShouldHaveAtLeastOneUuidTypeField() throws Exception {
		assertFalse(getActionType().getSimpleName() + " should have at least 1 UUID type field", getUuidFields().isEmpty());
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

	@Test
	public void entitysUuidFieldShouldBeStringType() throws Exception {
		for (final Field field : getUuidFields()) {
			assertEquals(String.class, getMatchingAliasField(field, getEntityType()).getType());
		}
	}

	@Test
	public void entitysUuidFieldShouldHaveConvertUsingAnnotation() throws Exception {
		for (final Field field : getUuidFields()) {
			assertNotNull(getMatchingAliasField(field, getEntityType()).getAnnotation(ConvertUsing.class));
		}
	}

	@Test
	public void entitysUuidFieldShouldHaveStringToUuidConverteAsValueForConvertUsingAnnotation() throws Exception {
		for (final Field field : getUuidFields()) {
			assertEquals(StringToUuidConverter.class, getMatchingAliasField(field, getEntityType()).getAnnotation(ConvertUsing.class).value());
		}
	}

	@Test
	public void entitysFieldsShouldHavePersistenceAnnotations() throws Exception {
		for (final Field field : getEntityType().getDeclaredFields()) {
			ModelActionEntityFieldAnnotationsTestUtils.assertField(field);
		}
	}

	@Test
	public void shouldNotHaveRepeatedColumnName() throws Exception {
		final HashSet<String> columnNames = new HashSet<String>();
		for (final Field field : getEntityType().getDeclaredFields()) {
			final Column column = field.getAnnotation(Column.class);
			if (column == null) continue;

			assertTrue("Should not have repeated name attribute of @Column at " + getEntityType().getSimpleName(), columnNames.add(column.name()));
		}
	}

	@Test
	public void actionShouldNotImplementModelActionInterfaceDirectly() throws Exception {
		assertFalse(getActionType()
				+ " should not implement ModelAction directly, please implement one of it's subTypes or create a new subType for the action",
				Arrays.asList(getActionType().getInterfaces()).contains(ModelAction.class));
	}

	@Test
	public void actionShouldBeMappedOnActionExecuter() throws Exception {
		try {
			ActionExecuter.executeAction(mock(ProjectContext.class), getActionType().newInstance());
		}
		catch (final UnableToCompleteActionException e) {
			assertFalse(e.getMessage(), ("There is no mapped action executer for " + getActionName() + ".").equals(e.getMessage()));
		}
		catch (final Exception e) {}
	}

	private List<Field> getUuidFields() {
		final List<Field> list = new ArrayList<Field>();
		for (final Field field : getActionType().getDeclaredFields()) {
			if (field.getType().equals(UUID.class)) list.add(field);
		}
		return list;
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

			assertNotNull("The field with name '" + aliasName + "' or the field annotated with @ConversionAlias('" + aliasName + "') was not found on "
					+ target.getSimpleName(),
					getMatchingAliasField(aliasName, target));
		}
	}

	protected Field getMatchingAliasField(final Field sourceField, final Class<?> target) {
		return getMatchingAliasField(getAliasName(sourceField), target);
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
