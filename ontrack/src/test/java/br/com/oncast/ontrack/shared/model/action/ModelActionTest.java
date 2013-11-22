package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.IgnoreByConversion;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.utils.actions.ModelActionEntityFieldAnnotationsTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class ModelActionTest {

	@Mock
	protected ProjectContext context;

	@Mock
	protected ActionContext actionContext;

	private UserRepresentation actionAuthor;

	@Before
	public void initContextMocks() throws Exception {
		MockitoAnnotations.initMocks(this);

		setActionAuthor(UserRepresentationTestUtils.getAdmin());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));
	}

	protected void setActionAuthor(final UserRepresentation user) throws UserNotFoundException {
		actionAuthor = user;
		final UUID userId = actionAuthor.getId();
		when(actionContext.getUserId()).thenReturn(userId);
		when(context.findUser(userId)).thenReturn(actionAuthor);
	}

	protected UserRepresentation getActionAuthor() {
		return actionAuthor;
	}

	protected ModelAction executeAction() throws UnableToCompleteActionException {
		return getNewInstance().execute(context, actionContext);
	}

	@SuppressWarnings("unchecked")
	protected <T extends Metadata> T captureAddedMetadata() {
		final ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
		verify(context).addMetadata(captor.capture());
		final Metadata value = captor.getValue();
		return (T) value;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Metadata> T captureRemovedMetadata() {
		final ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
		verify(context).removeMetadata(captor.capture());
		final Metadata value = captor.getValue();
		return (T) value;
	}

	@Test
	public void actionShouldHaveIdSetOnConstruction() throws Exception {
		assertNotNull(getNewInstance().getId());
	}

	@Test
	public void actionIdShouldBeDifferentForEachInstance() throws Exception {
		final ArrayList<UUID> idList = new ArrayList<UUID>();
		for (int i = 0; i < 15; i++) {
			final UUID id = getNewInstance().getId();
			assertFalse(idList.contains(id));
			idList.add(id);
		}
	}

	@Test
	public void actionShouldBeEqualToItsId() throws Exception {
		final ModelAction action = getNewInstance();
		assertEquals(action.getId(), action);
	}

	@Test
	public void actionShouldHaveSameHashCodeThanItsId() throws Exception {
		final ModelAction action = getNewInstance();
		assertEquals(action.getId().hashCode(), action.hashCode());
	}

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
		} catch (final NoSuchMethodException e) {
			fail(getActionName() + " should have a constructor with no arguments.");
		}
	}

	@Test
	public void actionsNoArgumentConstructorShouldHaveProtectedVisiblity() throws Exception {
		final Constructor<? extends ModelAction> noArgumentConstructor = getActionType().getDeclaredConstructor();
		assertTrue(Modifier.isProtected(noArgumentConstructor.getModifiers()));
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
		assertEquals(getActionNameWithoutPosfix(), getEntityType().getAnnotation(Entity.class).name());
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
	public void entitysNonStaticFieldsShouldHaveGetterAndSetter() throws Exception {
		final Class<?> type = getEntityType();
		try {
			for (final Field field : getAllNotInjectedFields(type)) {
				final String name = field.getName();
				final String methodSuffix = firstCharToUpperCase(name);
				type.getDeclaredMethod((isBooleanType(field) ? "is" : "get") + methodSuffix);
				type.getDeclaredMethod("set" + methodSuffix, field.getType());
			}
		} catch (final NoSuchMethodException e) {
			fail(getEntityType().getSimpleName() + " should have getters and setters for its fields");
		}
	}

	private String getActionNameWithoutPosfix() {
		return getActionName().replaceAll("Action$", "");
	}

	private boolean isBooleanType(final Field field) {
		return boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType());
	}

	private String firstCharToUpperCase(final String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
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
		for (final Field field : getAllNotInjectedFields(getEntityType())) {
			ModelActionEntityFieldAnnotationsTestUtils.assertField(getActionNameWithoutPosfix(), field);
		}
	}

	@Test
	public void shouldNotHaveRepeatedColumnNames() throws Exception {
		final HashSet<String> columnNames = new HashSet<String>();
		for (final Field field : getAllNotInjectedFields(getEntityType())) {
			final Column column = field.getAnnotation(Column.class);
			if (column == null) continue;

			assertTrue("Should not have repeated name attribute of @Column at " + getEntityType().getSimpleName(), columnNames.add(column.name()));
		}
	}

	@Test
	public void actionShouldNotImplementModelActionInterfaceDirectly() throws Exception {
		assertFalse(getActionType() + " should not implement ModelAction directly, please implement one of it's subTypes or create a new subType for the action",
				Arrays.asList(getActionType().getInterfaces()).contains(ModelAction.class));
	}

	@Test
	public void actionShouldBeMappedOnActionExecuter() throws Exception {
		try {
			ActionExecuter.executeAction(mock(ProjectContext.class), Mockito.mock(ActionContext.class), getNewInstance());
		} catch (final UnableToCompleteActionException e) {
			assertFalse(e.getMessage(), e.getMessage().contains("There is no mapped action executer"));
		} catch (final Exception e) {}
	}

	@Test
	public void shouldSetReferenceUUIDBeforeExecution() throws Exception {
		final ModelAction action = getNewInstance();
		assertNotNull(action.getReferenceId());
	}

	@Test
	public void shouldAddThisActionIntoUserActionTestUtilsCompleteActionsList() throws Exception {
		for (final UserAction action : UserActionTestUtils.createCompleteUserActionList()) {
			if (action.getModelAction().getClass().equals(getActionType())) return;
		}
		fail("UserActionTestUtils.createCompleteUserActionList() should contain a instance of " + getActionType().getSimpleName());
	}

	private Set<Field> getAllNotInjectedFields(final Class<?> type) {
		final HashSet<Field> fields = new HashSet<Field>();
		for (final Field field : type.getDeclaredFields()) {
			if (field.getName().contains("$")) continue; // IMPORTANT This skips fields injected by jacoco

			fields.add(field);
		}
		return fields;
	}

	private List<Field> getUuidFields() {
		final List<Field> list = new ArrayList<Field>();
		for (final Field field : getAllNotInjectedFields(getActionType())) {
			if (field.getType().equals(UUID.class)) list.add(field);
		}
		return list;
	}

	private void assertHasAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
		assertNotNull(type.getSimpleName() + " should be annotated with " + annotation.getSimpleName() + ".", type.getAnnotation(annotation));
	}

	private void assertConvertTo(final Class<?> source, final Class<?> target) {
		final ConvertTo annotation = source.getAnnotation(ConvertTo.class);
		assertEquals("ConvertTo annotation should have " + target.getSimpleName() + " as value.", target, annotation.value());
	}

	private void assertDontHaveNonStaticFinalFields(final Class<?> type) {
		for (final Field field : getAllNotInjectedFields(type)) {
			final int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers)) continue;

			assertFalse("The non static field '" + field.getName() + "' can't be final.", Modifier.isFinal(modifiers));
		}
	}

	private void assertAllNonStaticFieldsHaveMatchingConversionAlias(final Class<?> source, final Class<?> target) {
		for (final Field field : getAllNotInjectedFields(source)) {
			if (Modifier.isStatic(field.getModifiers()) || field.getAnnotation(IgnoreByConversion.class) != null) continue;

			final String aliasName = getAliasName(field);

			assertNotNull("The field with name '" + aliasName + "' or the field annotated with @ConversionAlias('" + aliasName + "') was not found on " + target.getSimpleName(),
					getMatchingAliasField(aliasName, target));
		}
	}

	private Field getMatchingAliasField(final Field sourceField, final Class<?> target) {
		return getMatchingAliasField(getAliasName(sourceField), target);
	}

	private Field getMatchingAliasField(final String aliasName, final Class<?> target) {
		for (final Field targetField : getAllNotInjectedFields(target)) {
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

	protected abstract ModelAction getNewInstance();

	protected abstract Class<? extends ModelAction> getActionType();

	protected abstract Class<? extends ModelActionEntity> getEntityType();
}
