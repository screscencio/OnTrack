package br.com.oncast.ontrack.shared.model.action;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEstimatedVelocityActionEntity;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseDeclareEstimatedVelocityActionTest extends ModelActionTest {

	private ProjectContext context;
	private UUID referenceId;
	private Release release;
	private Float declaredVelocity;
	private Float previouslyDeclaredEstimatedVelocity;

	@Before
	public void setUp() throws Exception {
		context = mock(ProjectContext.class);
		referenceId = new UUID();

		release = mock(Release.class);

		declaredVelocity = 13.4f;
		previouslyDeclaredEstimatedVelocity = 2.7f;

		when(context.findRelease(referenceId)).thenReturn(release);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldThrowExceptionWhenReferenceIdIsNull() throws Exception {
		when(context.findRelease(Mockito.any(UUID.class))).thenThrow(new ReleaseNotFoundException());

		new ReleaseDeclareEndDayAction(null, null).execute(context);
	}

	@Test
	public void shouldAccessTheReleaseReferencedByTheReferenceId() throws Exception {
		execute();
		verify(context).findRelease(referenceId);
	}

	@Test
	public void shouldDeclareTheGivenEndDayOnReferencedRelease() throws Exception {
		execute();
		verify(release).declareEstimatedVelocity(declaredVelocity);
	}

	@Test
	public void shouldBeAbleToDeclareEndDayAsNull() throws Exception {
		executeDeclaring(null);
		verify(release).declareEstimatedVelocity((Float) Mockito.isNull());
	}

	@Test
	public void undoActionShouldRevertDeclaredEndDayToNullWhenTheReleaseHadntAnyDeclaredEndDayBefore() throws Exception {
		when(release.hasDeclaredEstimatedVelocity()).thenReturn(false);
		when(release.getEstimatedVelocity()).thenReturn(previouslyDeclaredEstimatedVelocity);

		final ModelAction undoAction = execute();
		verify(release).declareEstimatedVelocity(Mockito.anyFloat());

		undoAction.execute(context);
		verify(release).declareEstimatedVelocity((Float) Mockito.isNull());
	}

	@Test
	public void undoActionShouldRevertDeclaredEndDayToPreviousValueWhenTheReleaseAlreadyHadADeclaredEndDayBefore() throws Exception {
		when(release.hasDeclaredEstimatedVelocity()).thenReturn(true);
		when(release.getEstimatedVelocity()).thenReturn(previouslyDeclaredEstimatedVelocity);

		final ModelAction undoAction = execute();
		verify(release).declareEstimatedVelocity(Mockito.anyFloat());

		undoAction.execute(context);
		verify(release).declareEstimatedVelocity(Mockito.eq(previouslyDeclaredEstimatedVelocity));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToDeclareZeroAsEstimatedVelocity() throws Exception {
		executeDeclaring(0f);
	}

	private ModelAction execute() throws UnableToCompleteActionException {
		return new ReleaseDeclareEstimatedVelocityAction(referenceId, declaredVelocity).execute(context);
	}

	private void executeDeclaring(final Float declaredVelocity) throws UnableToCompleteActionException {
		new ReleaseDeclareEstimatedVelocityAction(referenceId, declaredVelocity).execute(context);
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseDeclareEstimatedVelocityActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseDeclareEstimatedVelocityAction.class;
	}

}
