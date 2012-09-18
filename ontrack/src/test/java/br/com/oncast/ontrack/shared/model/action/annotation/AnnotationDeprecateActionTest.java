package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationDeprecateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationDeprecateActionTest extends ModelActionTest {

	private UUID subjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		annotation = AnnotationTestUtils.create(new Date(10));

		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);
		when(actionContext.getTimestamp()).thenReturn(new Date(100));
	}

	@Test
	public void shouldNotRemvoeTheAnnotation() throws Exception {
		executeAction();
		verify(context, never()).removeAnnotation(subjectId, annotation);
	}

	@Test
	public void shouldMarkTheReferencedAnntoationAsDeprecated() throws Exception {
		executeAction();
		assertTrue(annotation.isDeprecated());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToDeprecateAnInexistanteAnnotation() throws Exception {
		when(context.findAnnotation(subjectId, annotation.getId())).thenThrow(new AnnotationNotFoundException(""));
		executeAction();
	}

	@Test
	public void undoShouldRemoveDeprecationOfTheAnnotation() throws Exception {
		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);
		assertFalse(annotation.isDeprecated());
	}

	@Test
	public void shouldSetDeprecationTimestampOnAnnotation() throws Exception {
		final Date deprecationDate = new Date();
		when(actionContext.getTimestamp()).thenReturn(deprecationDate);
		executeAction();

		assertEquals(deprecationDate, annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));
	}

	@Test
	public void shouldSetDeprecationAuthorOnAnnotation() throws Exception {
		final User user = UserTestUtils.createUser();
		when(actionContext.getUserEmail()).thenReturn(user.getEmail());
		when(context.findUser(user.getEmail())).thenReturn(user);
		executeAction();

		assertEquals(user, annotation.getDeprecationAuthor(DeprecationState.DEPRECATED));
	}

	@Test
	public void shouldNotOverridePreviousDeprecationRemovalTimestamp() throws Exception {
		final User user = UserTestUtils.createUser();
		when(actionContext.getUserEmail()).thenReturn(user.getEmail());
		when(context.findUser(user.getEmail())).thenReturn(user);

		final ModelAction undoAction = executeAction();
		final Date deprecationRemovalTimestamp = new Date(1515);
		when(actionContext.getTimestamp()).thenReturn(deprecationRemovalTimestamp);
		final ModelAction redoAction = undoAction.execute(context, actionContext);

		final Date deprecationTimestamp = new Date(2020);
		when(actionContext.getTimestamp()).thenReturn(deprecationTimestamp);
		redoAction.execute(context, actionContext);

		assertEquals(deprecationRemovalTimestamp, annotation.getDeprecationTimestamp(DeprecationState.VALID));
		assertEquals(deprecationTimestamp, annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));

	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationDeprecateActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationDeprecateAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationDeprecateAction(subjectId, annotation.getId());
	}

}
