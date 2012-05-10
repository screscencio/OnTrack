package br.com.oncast.ontrack.shared.model.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareStartDayActionEntity;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.mocks.DateTestUtils;

public class ReleaseDeclareStartDayActionTest extends ModelActionTest {

	private UUID releaseId;
	private Release release;
	private ProjectContext context;
	private ArgumentCaptor<WorkingDay> workingDayCaptor;

	@Before
	public void setUp() throws Exception {
		workingDayCaptor = ArgumentCaptor.forClass(WorkingDay.class);
		release = mock(Release.class);
		releaseId = new UUID();

		context = mock(ProjectContext.class);
		when(context.findRelease(releaseId)).thenReturn(release);
	}

	@Test
	public void shouldDeclareStartDayOnTheReleaseWithTheReferenceId() throws Exception {
		final Date declaredDay = DateTestUtils.newDate(2011, 05, 15);

		new ReleaseDeclareStartDayAction(releaseId, declaredDay).execute(context);

		verify(release).declareStartDay(workingDayCaptor.capture());

		assertEquals(WorkingDayFactory.create(declaredDay), workingDayCaptor.getValue());
	}

	@Test
	public void rollbackActionShouldOperateOverTheSameRelease() throws Exception {
		final ModelAction rollbackAction = new ReleaseDeclareStartDayAction(releaseId, new Date()).execute(context);

		final ProjectContext otherContext = mock(ProjectContext.class);
		when(otherContext.findRelease(releaseId)).thenReturn(release);
		rollbackAction.execute(otherContext);

		verify(otherContext).findRelease(releaseId);
	}

	@Test
	public void shouldRollbackStartDayToNullWhenThereWasNoDeclaredStartDayBefore() throws Exception {
		when(release.hasDeclaredStartDay()).thenReturn(false);

		final ModelAction rollbackAction = new ReleaseDeclareStartDayAction(releaseId, new Date()).execute(context);

		rollbackAction.execute(context);

		verify(release, times(2)).declareStartDay(workingDayCaptor.capture());
		verify(release, never()).getStartDay();

		assertNull(workingDayCaptor.getValue());
	}

	@Test
	public void shouldRollbackStartDayToPreviousWorkingDayWhenThereWasADeclaredStartDayBefore() throws Exception {
		final WorkingDay previousDate = WorkingDayFactory.create(1987, 12, 21);

		when(release.hasDeclaredStartDay()).thenReturn(true);
		when(release.getStartDay()).thenReturn(previousDate);

		final ModelAction rollbackAction = new ReleaseDeclareStartDayAction(releaseId, new Date()).execute(context);
		verify(release).getStartDay();

		rollbackAction.execute(context);

		verify(release, times(2)).declareStartDay(workingDayCaptor.capture());
		assertEquals(previousDate, workingDayCaptor.getValue());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseDeclareStartDayActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseDeclareStartDayAction.class;
	}
}
