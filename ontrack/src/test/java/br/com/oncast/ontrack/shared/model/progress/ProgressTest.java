package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ProgressTest {

	private Progress progress;

	@Before
	public void setUp() {
		progress = new Progress();
	}

	@Test
	public void shouldSetStartDateOfProgressWhenUnderWorkIsSet() {
		assertNull(progress.getStartDay());
		progress.setState(ProgressState.UNDER_WORK);

		assertEquals(WorkingDayFactory.create(), progress.getStartDay());
	}

	@Test
	public void shouldSetStartDateOfProgressWhenDoneIsSet() {
		assertNull(progress.getStartDay());
		progress.setState(ProgressState.DONE);

		assertEquals(WorkingDayFactory.create(), progress.getStartDay());
	}

	@Test
	public void shouldNotChangeStartDateOfProgressIfItIsAlreadySet() {
		assertNull(progress.getStartDay());
		progress.setState(ProgressState.UNDER_WORK);

		final WorkingDay startDate = progress.getStartDay();
		assertNotNull(startDate);

		for (final ProgressState newState : ProgressState.values()) {
			progress.setState(newState);
			assertEquals(startDate, progress.getStartDay());
		}
	}

	@Test
	public void shouldSetEndDateOfProgressWhenDoneIsSet() {
		assertNull(progress.getEndDay());
		progress.setState(ProgressState.DONE);

		assertEquals(WorkingDayFactory.create(), progress.getEndDay());
	}

	@Test
	public void shouldSetEndDateOfProgressEvenIfDoneIsAlreadySet() throws InterruptedException {
		assertNull(progress.getEndDay());

		progress.setState(ProgressState.DONE);
		final WorkingDay endDate = progress.getEndDay();
		assertNotNull(endDate);

		progress.setState(ProgressState.DONE);
		assertFalse(endDate == progress.getEndDay());
	}

	@Test
	public void shouldResetEndDateOfProgressWhenStateChangesToNotStartedOrUnderWork() {
		assertNull(progress.getEndDay());

		progress.setState(ProgressState.DONE);
		assertNotNull(progress.getEndDay());

		for (final ProgressState newState : ProgressState.values()) {
			if (newState != ProgressState.DONE) {
				progress.setState(newState);
				assertNull(progress.getEndDay());
				progress.setState(ProgressState.DONE);
			}
		}
	}

	@Test
	public void getStartDayShouldReturnACopyOfStartDay() throws Exception {
		progress.setState(ProgressState.DONE);
		final WorkingDay startDay = progress.getStartDay();
		assertNotSame(startDay, progress.getStartDay());

		startDay.add(5);
		assertFalse(startDay.equals(progress.getStartDay()));
	}

	@Test
	public void getEndDayShouldReturnACopyOfEndDay() throws Exception {
		progress.setState(ProgressState.DONE);
		final WorkingDay endDay = progress.getEndDay();
		assertNotSame(endDay, progress.getEndDay());

		endDay.add(12);
		assertFalse(endDay.equals(progress.getEndDay()));
	}

	@Test
	public void shouldUseTheLastUpdateTimestampToSetTheStartDay() throws Exception {
		final Date timestamp = new Date(12345);
		setLastUpdateTimstamp(timestamp);
		progress.setState(ProgressState.UNDER_WORK);

		final WorkingDay expectedStartDay = WorkingDayFactory.create(timestamp);
		assertEquals(expectedStartDay, progress.getStartDay());
	}

	@Test
	public void shouldUseTheLastUpdateTimestampToSetTheEndDay() throws Exception {
		final Date timestamp = new Date(12345);
		setLastUpdateTimstamp(timestamp);
		progress.setState(ProgressState.DONE);

		final WorkingDay expectedEndDay = WorkingDayFactory.create(timestamp);
		assertEquals(expectedEndDay, progress.getEndDay());
	}

	private void setLastUpdateTimstamp(final Date timestamp) throws Exception {
		final Field field = progress.getClass().getDeclaredField("lastUpdateTimestamp");
		field.setAccessible(true);
		field.set(progress, timestamp);
	}
}
