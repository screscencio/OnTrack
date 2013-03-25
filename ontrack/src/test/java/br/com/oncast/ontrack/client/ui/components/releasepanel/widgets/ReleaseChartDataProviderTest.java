package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartDataProvider;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.gwt.user.client.rpc.impl.ReflectionHelper;
import com.ibm.icu.util.Calendar;

public class ReleaseChartDataProviderTest {

	private ReleaseEstimator estimatorMock;
	private static Release release;

	private WorkingDay estimatedEndDay;
	private WorkingDay estimatedStartDay;
	private Float releaseEffortSum;
	private ActionExecutionService actionExecutionServiceMock;

	@Before
	public void setup() throws Exception {
		release = Mockito.spy(ReleaseTestUtils.createRelease());
		releaseEffortSum = 10f;
		setupReleaseMock();

		estimatorMock = Mockito.mock(ReleaseEstimator.class);
		estimatedStartDay = WorkingDayFactory.create();
		estimatedEndDay = WorkingDayFactory.create().add(5);
		setupEstimatorMock();

		actionExecutionServiceMock = Mockito.mock(ActionExecutionService.class);
	}

	public void verifyMocks() {
		Mockito.verify(estimatorMock, Mockito.atLeastOnce()).getEstimatedEndDayFor(release);
		Mockito.verify(estimatorMock, Mockito.atLeastOnce()).getEstimatedStartDayFor(release);
		Mockito.verify(release, Mockito.atLeastOnce()).getAllScopesIncludingDescendantReleases();
	}

	@Test
	public void releaseDaysShouldHaveOnlyTheEstimatedDayWhenEstimatedStartDayAndEstimatedEndDayAreEqual() throws Exception {
		estimatedEndDay = estimatedStartDay.copy();
		for (int i = 0; i < 10; i++) {
			estimatedStartDay.add(i);
			estimatedEndDay.add(i);
			assertEquals(estimatedStartDay, estimatedEndDay);
			assertReleaseDays(estimatedEndDay);
		}
		verifyMocks();
	}

	@Test
	public void releaseDaysShouldStartOnReleaseStartDay() throws Exception {
		assertEquals(estimatedStartDay.getDayAndMonthString(), getProvider().getReleaseDays().get(0).getDayAndMonthString());
		verifyMocks();
	}

	@Test
	public void releaseDaysShouldEndOnEstimatedEndDayWhenReleaseEndDayIsNull() throws Exception {
		final List<WorkingDay> releaseDays = getProvider().getReleaseDays();
		final int lastIndex = releaseDays.size() - 1;
		assertNull(release.getEndDay());
		assertEquals(estimatedEndDay, releaseDays.get(lastIndex));
		verifyMocks();
	}

	@Test
	public void releaseDaysShouldEndOnEstimatedEndDayWhenEstimatedEndDayIsAfterTheReleaseEndDay() throws Exception {
		Accomplish.effortPoints(1).on(estimatedEndDay.copy().add(-1));

		final List<WorkingDay> releaseDays = getProvider().getReleaseDays();
		final int lastIndex = releaseDays.size() - 1;

		assertTrue(estimatedEndDay.isAfter(release.getEndDay()));
		assertEquals(estimatedEndDay.getDayAndMonthString(), releaseDays.get(lastIndex).getDayAndMonthString());
		verifyMocks();
	}

	@Test
	public void releaseDaysShouldEndOnReleaseEndDayWhenTheReleaseEndDayIsAfterEstimatedEndDay() throws Exception {
		Accomplish.effortPoints(1).on(estimatedEndDay.copy().add(1));

		final List<WorkingDay> releaseDays = getProvider().getReleaseDays();
		final int lastIndex = releaseDays.size() - 1;

		final WorkingDay releaseEndDay = release.getEndDay();
		assertTrue(releaseEndDay.isAfter(estimatedEndDay));
		assertEquals(releaseEndDay.getDayAndMonthString(), releaseDays.get(lastIndex).getDayAndMonthString());
		verifyMocks();
	}

	@Test
	public void releaseDaysShouldContainAllDaysFromStartDayToEndDayInOrder() throws Exception {
		estimatedStartDay = WorkingDayFactory.create(2011, Calendar.JANUARY, 3);
		estimatedEndDay = WorkingDayFactory.create(2011, Calendar.JANUARY, 5);
		assertReleaseDays("03/01", "04/01", "05/01");
		verifyMocks();
	}

	@Test
	public void getEffortSumShouldReturnEffortSumOfTheRelease() throws Exception {
		for (int i = 0; i < 20; i++) {
			releaseEffortSum = (float) i;
			assertEquals(releaseEffortSum, getProvider().getEffortSum());
		}
	}

	@Test
	public void getEstimatedEndDayShouldReturnTheReleaseEstimatorsEstimatedEndDay() throws Exception {
		for (int i = 0; i < 20; i++) {
			estimatedEndDay.add(i);
			assertEquals(estimatedEndDay, getProvider().getEstimatedEndDay());
		}
		Mockito.verify(estimatorMock, Mockito.atLeast(20)).getEstimatedEndDayFor(release);
	}

	@Test
	public void accomplishedEffortByDateShouldHaveOnlyOneZeroWhenReleaseEffortSumIsZero() throws Exception {
		releaseEffortSum = 0f;
		assertAccomplishedEffortsByDate(0f);
		estimatedStartDay = WorkingDayFactory.create(2011, Calendar.JANUARY, 3);
		setReleaseDuration(3);
		assertAccomplishedEffortsByDate(0f);
		verifyMocks();
	}

	@Test
	public void shouldNotHaveAccomplishedEffortAfterToday() throws Exception {
		setReleaseDuration(20);
		Accomplish.effortPoints(5).today();
		Accomplish.effortPoints(13).on(WorkingDayFactory.create().add(5));

		assertAccomplishedEffortsByDate(5f);
		verifyMocks();
	}

	@Test
	public void shouldNotReplicateAccomplishedEffortAfterReachingTheReleaseEffortSum() throws Exception {
		releaseEffortSum = 10f;
		final WorkingDay startDay = WorkingDayFactory.create().add(-10);
		estimatedStartDay = startDay.copy();
		setReleaseDuration(5);
		Accomplish.effortPoints(5).on(startDay);
		Accomplish.effortPoints(5).on(startDay.copy().add(2));

		assertAccomplishedEffortsByDate(5f, 5f, 10f);
		verifyMocks();
	}

	@Test
	public void getEstimatedVelocityShouldReturnTheDeclaredOneWhenAlreadyDeclared() throws Exception {
		final Float declaredVelocity = 1.5f;
		Mockito.when(release.hasDeclaredEstimatedVelocity()).thenReturn(true);
		Mockito.when(release.getEstimatedVelocity()).thenReturn(declaredVelocity);

		assertEquals(declaredVelocity, getProvider().getEstimatedVelocity());

		Mockito.verify(release).getEstimatedVelocity();
	}

	@Test
	public void getEstimatedVelocityShouldReturnTheInferedOneWhenNotDeclared() throws Exception {
		final Float inferedVelocity = 5.6f;

		Mockito.when(release.hasDeclaredEstimatedVelocity()).thenReturn(false);
		Mockito.when(estimatorMock.getInferedEstimatedVelocity(Mockito.any(Release.class))).thenReturn(inferedVelocity);

		assertEquals(inferedVelocity, getProvider().getEstimatedVelocity());

		Mockito.verify(release, Mockito.never()).getEstimatedVelocity();
		Mockito.verify(estimatorMock).getInferedEstimatedVelocity(Mockito.any(Release.class));
	}

	@Test
	public void shouldRequestReleaseDeclareStartDayActionWhenDeclareStartDayWereCalled() throws Exception {
		final Date declaredDate = new Date();
		final UUID releaseId = new UUID();
		when(release.getId()).thenReturn(releaseId);

		getProvider().declareStartDate(declaredDate);

		final ArgumentCaptor<ReleaseDeclareStartDayAction> captor = ArgumentCaptor.forClass(ReleaseDeclareStartDayAction.class);
		verify(actionExecutionServiceMock).onUserActionExecutionRequest(captor.capture());
		verify(release).getId();

		final ReleaseDeclareStartDayAction action = captor.getValue();
		assertEquals(releaseId, action.getReferenceId());
		assertEquals(declaredDate, ReflectionHelper.getField(ReleaseDeclareStartDayAction.class, action, "date"));
	}

	@Test
	public void shouldRequestReleaseDeclareEndDayActionWhenDeclareEndDayWereCalled() throws Exception {
		final Date declaredDate = new Date();
		final UUID releaseId = new UUID();
		when(release.getId()).thenReturn(releaseId);

		getProvider().declareEndDate(declaredDate);

		final ArgumentCaptor<ReleaseDeclareEndDayAction> captor = ArgumentCaptor.forClass(ReleaseDeclareEndDayAction.class);
		verify(actionExecutionServiceMock).onUserActionExecutionRequest(captor.capture());
		verify(release).getId();

		final ReleaseDeclareEndDayAction action = captor.getValue();
		assertEquals(releaseId, action.getReferenceId());
		assertEquals(declaredDate, ReflectionHelper.getField(ReleaseDeclareEndDayAction.class, action, "endDay"));
	}

	@Test
	public void shouldRequestReleaseDeclareEstimatedVelocityActionWhenDeclareEstimatedVelocityWereCalled() throws Exception {
		final Float declaredVelocity = 12.5f;
		final UUID releaseId = new UUID();
		when(release.getId()).thenReturn(releaseId);

		getProvider().declareEstimatedVelocity(declaredVelocity);

		final ArgumentCaptor<ReleaseDeclareEstimatedVelocityAction> captor = ArgumentCaptor.forClass(ReleaseDeclareEstimatedVelocityAction.class);
		verify(actionExecutionServiceMock).onUserActionExecutionRequest(captor.capture());
		verify(release).getId();

		final ReleaseDeclareEstimatedVelocityAction action = captor.getValue();
		assertEquals(releaseId, action.getReferenceId());
		assertEquals(declaredVelocity, ReflectionHelper.getField(ReleaseDeclareEstimatedVelocityAction.class, action, "estimatedVelocity"));
	}

	@Test
	public void shouldConsiderDescendantScopesProgressOnAccomplishedEffortCalculation() throws Exception {
		final WorkingDay day2 = WorkingDayFactory.create(2012, 6, 2);
		final WorkingDay day3 = WorkingDayFactory.create(2012, 6, 3);
		final WorkingDay day5 = WorkingDayFactory.create(2012, 6, 5);
		final WorkingDay day6 = WorkingDayFactory.create(2012, 6, 6);

		final Scope parent = ScopeTestUtils.createScope();
		final Scope child1 = ScopeTestUtils.createScope("child1", ProgressState.DONE, 1, day2, day5);
		final Scope child3 = ScopeTestUtils.createScope("child2", ProgressState.DONE, 2, day2, day6);
		final Scope child2 = ScopeTestUtils.createScope("child3", ProgressState.DONE, 3, day3, day3);

		parent.add(child1);
		parent.add(child2);
		parent.add(child3);

		release.addScope(parent);

		releaseEffortSum = 6F;
		estimatedStartDay = day2;
		estimatedEndDay = WorkingDayFactory.create(2012, 6, 7);

		assertAccomplishedEffortsByDate(0f, 3f, 3f, 4f, 6f);
	}

	@Test
	public void inferenceEngineShouldNotMessWithTheBurnUpDates() throws Exception {
		final WorkingDay parentCreation = WorkingDayFactory.create(2012, 6, 2);
		estimatedStartDay = WorkingDayFactory.create(2012, 6, 3);

		final WorkingDay childBAccomplishDay = WorkingDayFactory.create(2012, 6, 5);
		estimatedEndDay = WorkingDayFactory.create(2012, 6, 6);
		releaseEffortSum = 10F;

		final Scope parent = ScopeTestUtils.createScope(parentCreation);
		ScopeTestUtils.setDelcaredEffort(parent, releaseEffortSum);

		final Scope childB = ScopeTestUtils.createScope(parentCreation);
		parent.add(childB);
		processInference(parent, parentCreation);

		final Scope childA = ScopeTestUtils.createScope(estimatedStartDay);
		parent.add(childA);
		processInference(parent, estimatedStartDay);

		ScopeTestUtils.setProgress(childA, ProgressState.UNDER_WORK, estimatedStartDay);
		processInference(parent, estimatedStartDay);

		ScopeTestUtils.setProgress(childB, ProgressState.DONE, childBAccomplishDay);
		processInference(parent, childBAccomplishDay);

		ScopeTestUtils.setProgress(childA, ProgressState.DONE, estimatedEndDay);
		processInference(parent, estimatedEndDay);

		release.addScope(parent);
		assertAccomplishedEffortsByDate(0f, 0f, 5f, 10f);
	}

	private void processInference(final Scope scope, final WorkingDay day) {
		final UserRepresentation admin = UserRepresentationTestUtils.getAdmin();
		new ProgressInferenceEngine().process(scope, admin, day.getJavaDate());
		new EffortInferenceEngine().process(scope, admin, day.getJavaDate());
	}

	private void setReleaseDuration(final int nDays) {
		estimatedEndDay = estimatedStartDay.copy().add(nDays - 1);
	}

	private void assertAccomplishedEffortsByDate(final Float... efforts) {
		final List<Float> list = new ArrayList<Float>(getProvider().getAccomplishedEffortPointsByDate().values());
		assertArrayEquals(efforts, list.toArray());
	}

	private void assertReleaseDays(final WorkingDay... days) {
		final List<WorkingDay> list = getProvider().getReleaseDays();
		assertEquals(days.length, list.size());
		for (int i = 0; i < days.length; i++) {
			assertEquals(days[i].getDayAndMonthString(), list.get(i).getDayAndMonthString());
		}

	}

	private void assertReleaseDays(final String... days) {
		final List<WorkingDay> list = getProvider().getReleaseDays();
		assertEquals(days.length, list.size());
		for (int i = 0; i < days.length; i++) {
			assertEquals(days[i], list.get(i).getDayAndMonthString());
		}
	}

	private void setupEstimatorMock() {
		Mockito.when(estimatorMock.getEstimatedStartDayFor(release)).thenAnswer(new Answer<WorkingDay>() {

			@Override
			public WorkingDay answer(final InvocationOnMock invocation) throws Throwable {
				return estimatedStartDay.copy();
			}
		});
		Mockito.when(estimatorMock.getEstimatedEndDayFor(release)).thenAnswer(new Answer<WorkingDay>() {

			@Override
			public WorkingDay answer(final InvocationOnMock invocation) throws Throwable {
				return estimatedEndDay.copy();
			}
		});
	}

	private void setupReleaseMock() {
		Mockito.when(release.getEffortSum()).thenAnswer(new Answer<Float>() {

			@Override
			public Float answer(final InvocationOnMock invocation) throws Throwable {
				return releaseEffortSum;
			}
		});
	}

	private ReleaseChartDataProvider getProvider() {
		return new ReleaseChartDataProvider(release, estimatorMock, actionExecutionServiceMock);
	}

	private static class Accomplish {

		private final Scope scope;

		public Accomplish(final int effort) {
			scope = createScope(ProgressState.UNDER_WORK, effort);
			release.addScope(scope);
		}

		public void today() {
			ScopeTestUtils.setProgress(scope, ProgressState.DONE);
		}

		public static Accomplish effortPoints(final int effort) {
			return new Accomplish(effort);
		}

		public void on(final WorkingDay workingDay) throws Exception {
			ScopeTestUtils.setEndDate(scope, workingDay);
		}

		private static Scope createScope(final ProgressState progress, final int effort) {
			final Scope scope = ScopeTestUtils.createScope("Scope " + effort);
			ScopeTestUtils.setProgress(scope, progress);
			ScopeTestUtils.setDelcaredEffort(scope, effort);
			return scope;
		}

	}
}
