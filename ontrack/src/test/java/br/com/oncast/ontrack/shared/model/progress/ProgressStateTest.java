package br.com.oncast.ontrack.shared.model.progress;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgressStateTest {

	@Test
	public void NOT_STARTED_ShouldBeKnowHowToString() {
		assertEquals("", ProgressState.NOT_STARTED.toString());
	}

	@Test
	public void UNDER_WORK_ShouldBeKnowHowToString() {
		assertEquals("Under work", ProgressState.UNDER_WORK.toString());
	}

	@Test
	public void DONE_ShouldBeKnowHowToString() {
		assertEquals("Done", ProgressState.DONE.toString());
	}

	@Test
	public void shouldBeKnowHowToGetLabelForDescriptionWhenDescriptionOnParameterMatchTheDescriptionOfNotStartedState() {
		assertEquals("Not Started", ProgressState.getLabelForDescription("Not started"));
	}

	@Test
	public void shouldBeKnowHowToGetLabelForDescriptionWhenDescriptionOnParameterDoesNotMatchTheDescriptionOfNotStartedState() {
		assertEquals("XPTO", ProgressState.getLabelForDescription("XPTO"));
	}
}
