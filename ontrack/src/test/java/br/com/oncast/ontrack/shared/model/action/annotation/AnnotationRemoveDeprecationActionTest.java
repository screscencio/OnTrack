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

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveDeprecationActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveDeprecationAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class AnnotationRemoveDeprecationActionTest extends ModelActionTest {

	private UUID subjectId;
	private Annotation annotation;
	private Date deprecationTimestamp;
	private User deprecationAuthor;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		annotation = AnnotationTestUtils.create();
		deprecationTimestamp = new Date();
		deprecationAuthor = UserTestUtils.createUser();
		annotation.setDeprecation(DeprecationState.DEPRECATED, deprecationAuthor, deprecationTimestamp);

		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);
		when(actionContext.getTimestamp()).thenReturn(deprecationTimestamp);
	}

	@Test
	public void shouldNotAddTheAnnotation() throws Exception {
		executeAction();
		verify(context, never()).addAnnotation(subjectId, annotation);
	}

	@Test
	public void shouldMarkTheReferencedAnntoationAsNotDeprecated() throws Exception {
		executeAction();
		assertFalse(annotation.isDeprecated());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveDeprecationOfAnInexistanteAnnotation() throws Exception {
		when(context.findAnnotation(subjectId, annotation.getId())).thenThrow(new AnnotationNotFoundException(""));
		executeAction();
	}

	@Test
	public void undoShouldDeprecateTheAnnotation() throws Exception {
		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);
		assertTrue(annotation.isDeprecated());
	}

	@Test
	public void shouldSetDeprecationTimestampOnAnnotation() throws Exception {
		final Date deprecationRemovalDate = new Date();
		when(actionContext.getTimestamp()).thenReturn(deprecationRemovalDate);
		executeAction();

		assertEquals(deprecationTimestamp, annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));
		assertEquals(deprecationRemovalDate, annotation.getDeprecationTimestamp(DeprecationState.VALID));
	}

	@Test
	public void shouldSetDeprecationAuthorOnAnnotation() throws Exception {
		final User deprecationRemovalAuthor = UserTestUtils.createUser();
		when(actionContext.getUserId()).thenReturn(deprecationRemovalAuthor.getId());
		when(context.findUser(deprecationRemovalAuthor.getId())).thenReturn(deprecationRemovalAuthor);
		executeAction();

		assertEquals(deprecationAuthor, annotation.getDeprecationAuthor(DeprecationState.DEPRECATED));
		assertEquals(deprecationRemovalAuthor, annotation.getDeprecationAuthor(DeprecationState.VALID));
	}

	@Test
	public void shouldNotOverrideDeprecationTime() throws Exception {

	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationRemoveDeprecationActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationRemoveDeprecationAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationRemoveDeprecationAction(subjectId, annotation.getId());
	}

}
