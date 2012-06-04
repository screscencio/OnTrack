package br.com.oncast.ontrack.shared.model.action.release;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEndDayActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.mocks.DateTestUtils;

public class ReleaseDeclareEndDayActionTest extends ModelActionTest {

	private ProjectContext context;
	private UUID referenceId;
	private Release release;
	private Date declaredDay;
	private WorkingDay previouslyDeclaredDay;

	@Before
	public void setUp() throws Exception {
		context = mock(ProjectContext.class);
		referenceId = new UUID();

		release = mock(Release.class);

		declaredDay = DateTestUtils.newDate(2004, 1, 14);
		previouslyDeclaredDay = WorkingDayFactory.create(2012, 12, 25);

		when(context.findRelease(referenceId)).thenReturn(release);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldThrowExceptionWhenReferenceIdIsNull() throws Exception {
		when(context.findRelease(Mockito.any(UUID.class))).thenThrow(new ReleaseNotFoundException());

		new ReleaseDeclareEndDayAction(null, null).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test
	public void shouldAccessTheReleaseReferencedByTheReferenceId() throws Exception {
		execute();
		verify(context).findRelease(referenceId);
	}

	@Test
	public void shouldDeclareTheGivenEndDayOnReferencedRelease() throws Exception {
		execute();
		verify(release).declareEndDay(Mockito.eq(WorkingDayFactory.create(declaredDay)));
	}

	@Test
	public void shouldBeAbleToDeclareEndDayAsNull() throws Exception {
		executeDeclaring(null);
		verify(release).declareEndDay((WorkingDay) Mockito.isNull());
	}

	@Test
	public void undoActionShouldRevertDeclaredEndDayToNullWhenTheReleaseHadntAnyDeclaredEndDayBefore() throws Exception {
		when(release.hasDeclaredEndDay()).thenReturn(false);
		when(release.getEndDay()).thenReturn(previouslyDeclaredDay);

		final ModelAction undoAction = execute();
		verify(release).declareEndDay(Mockito.any(WorkingDay.class));

		undoAction.execute(context, Mockito.mock(ActionContext.class));
		verify(release).declareEndDay((WorkingDay) Mockito.isNull());
	}

	@Test
	public void undoActionShouldRevertDeclaredEndDayToPreviousValueWhenTheReleaseAlreadyHadADeclaredEndDayBefore() throws Exception {
		when(release.hasDeclaredEndDay()).thenReturn(true);
		when(release.getEndDay()).thenReturn(previouslyDeclaredDay);

		final ModelAction undoAction = execute();
		verify(release).declareEndDay(Mockito.any(WorkingDay.class));

		undoAction.execute(context, Mockito.mock(ActionContext.class));
		verify(release).declareEndDay(Mockito.eq(previouslyDeclaredDay));
	}

	private ModelAction execute() throws UnableToCompleteActionException {
		return new ReleaseDeclareEndDayAction(referenceId, declaredDay).execute(context, Mockito.mock(ActionContext.class));
	}

	private void executeDeclaring(final Date declaredDay) throws UnableToCompleteActionException {
		new ReleaseDeclareEndDayAction(referenceId, declaredDay).execute(context, Mockito.mock(ActionContext.class));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseDeclareEndDayActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseDeclareEndDayAction.class;
	}

	@Override
	protected ModelAction getInstance() {
		return new ReleaseDeclareEndDayAction(new UUID(), new Date());
	}

}
