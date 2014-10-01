package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.client.utils.date.DateUnit;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

public class ProgressTest {

	private Progress progress;

	@Before
	public void setUp() {
		progress = ProgressTestUtils.create();
	}

	@Test
	public void shouldSetStartDateOfProgressWhenUnderWorkIsSet() {
		assertNull(progress.getStartDay());
		setState(ProgressState.UNDER_WORK);

		assertEquals(WorkingDayFactory.create(), progress.getStartDay());
	}

	@Test
	public void shouldSetStartDateOfProgressWhenDoneIsSet() {
		assertNull(progress.getStartDay());
		setState(ProgressState.DONE);

		assertEquals(WorkingDayFactory.create(), progress.getStartDay());
	}

	@Test
	public void shouldNotChangeStartDateOfProgressIfItIsAlreadySet() {
		assertNull(progress.getStartDay());
		setState(ProgressState.UNDER_WORK);

		final WorkingDay startDate = progress.getStartDay();
		assertNotNull(startDate);

		for (final ProgressState newState : ProgressState.values()) {
			setState(newState);
			assertEquals(startDate, progress.getStartDay());
		}
	}

	@Test
	public void shouldSetEndDateOfProgressWhenDoneIsSet() {
		assertNull(progress.getEndDay());
		setState(ProgressState.DONE);

		assertEquals(WorkingDayFactory.create(), progress.getEndDay());
	}

	@Test
	public void shouldSetEndDateOfProgressEvenIfDoneIsAlreadySet() throws InterruptedException {
		assertNull(progress.getEndDay());

		setState(ProgressState.DONE);
		final WorkingDay endDate = progress.getEndDay();
		assertNotNull(endDate);

		setState(ProgressState.DONE);
		assertFalse(endDate == progress.getEndDay());
	}

	@Test
	public void shouldResetEndDateOfProgressWhenStateChangesToNotStartedOrUnderWork() {
		assertNull(progress.getEndDay());

		setState(ProgressState.DONE);
		assertNotNull(progress.getEndDay());

		for (final ProgressState newState : ProgressState.values()) {
			if (newState != ProgressState.DONE) {
				setState(newState);
				assertNull(progress.getEndDay());
				setState(ProgressState.DONE);
			}
		}
	}

	@Test
	public void getStartDayShouldReturnACopyOfStartDay() throws Exception {
		setState(ProgressState.DONE);
		final WorkingDay startDay = progress.getStartDay();
		assertNotSame(startDay, progress.getStartDay());

		startDay.add(5);
		assertFalse(startDay.equals(progress.getStartDay()));
	}

	@Test
	public void getEndDayShouldReturnACopyOfEndDay() throws Exception {
		setState(ProgressState.DONE);
		final WorkingDay endDay = progress.getEndDay();
		assertNotSame(endDay, progress.getEndDay());

		endDay.add(12);
		assertFalse(endDay.equals(progress.getEndDay()));
	}

	@Test
	public void getLeadTime_shouldReturnNullIfIsNotDone() {
		setState(ProgressState.UNDER_WORK);
		assertNull(progress.getLeadTime());
	}

	@Test
	public void getLeadTime_shouldNotReturnNullIfIsDone() {
		setState(ProgressState.DONE);
		assertNotNull(progress.getLeadTime());
	}

	@Test
	public void getLeadTime_shouldReturnTheCorrectLeadTimeIfIsDone() {
		final Progress newProgress = ProgressTestUtils.create();
		final Date date = new Date();
		CalendarUtil.addDaysToDate(date, 3);
		newProgress.setState(ProgressState.DONE, UserRepresentationTestUtils.getAdmin(), date);
		assertEquals(3l, (newProgress.getLeadTime().longValue() / DateUnit.DAY));
	}

	@Test
	public void getCycleTime_shouldReturnNullIfIsNotDone() {
		setState(ProgressState.UNDER_WORK);
		assertNull(progress.getCycleTime());
	}

	@Test
	public void getCycleTime_shouldNotReturnNullIfIsDone() {
		setState(ProgressState.DONE);
		assertNotNull(progress.getCycleTime());
	}

	@Test
	public void getCycleTime_shouldReturnTheCorrectCycleTimeIfIsDone() {
		final Progress newProgress = ProgressTestUtils.create();
		final Date dateWork = new Date();
		CalendarUtil.addDaysToDate(dateWork, 1);
		final Date dateDone = new Date();
		CalendarUtil.addDaysToDate(dateDone, 3);
		newProgress.setState(ProgressState.UNDER_WORK, UserRepresentationTestUtils.getAdmin(), dateWork);
		newProgress.setState(ProgressState.DONE, UserRepresentationTestUtils.getAdmin(), dateDone);
		assertEquals(2l, (newProgress.getCycleTime().longValue() / DateUnit.DAY));
	}

	private void setState(final ProgressState state) {
		try {
			Thread.sleep(1);
		} catch (final InterruptedException e) {}
		progress.setState(state, UserRepresentationTestUtils.getAdmin(), new Date());
	}
}
