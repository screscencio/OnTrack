package br.com.oncast.ontrack.shared.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

public class ModelStateManagerTest {

	private String initialStateValue;
	private ModelStateManager<String> manager;
	private Date initialTimestamp;
	private UserRepresentation initialAuthor;

	@Before
	public void setup() throws Exception {
		initialStateValue = "initialState";
		initialTimestamp = new Date(1000);
		initialAuthor = UserRepresentationTestUtils.createUser();
		manager = new ModelStateManager<String>(initialStateValue, initialAuthor, initialTimestamp);
	}

	@Test
	public void theInitialStateShouldBeTheCurrentStateValue() throws Exception {
		assertEquals(initialStateValue, manager.getCurrentStateValue());
	}

	@Test
	public void theInitialStateTimestampShouldBeRecorded() throws Exception {
		assertEquals(initialTimestamp, manager.getCurrentState().getTimestamp());
	}

	@Test
	public void theInitialStateChangeEventAuthorShouldBeRecorded() throws Exception {
		assertEquals(initialAuthor, manager.getCurrentState().getAuthor());
	}

	@Test
	public void itsPossibleToSetAsInitialStateAStateThatIsAlreadtCreated() throws Exception {
		final ModelState<Boolean> state = ModelState.create(false, initialAuthor, initialTimestamp);
		final ModelStateManager<Boolean> manager = new ModelStateManager<Boolean>(state);
		assertEquals(state, manager.getCurrentState());
	}

	@Test
	public void theCurrentStateIsTheNewStateWhenNewStateIsSet() throws Exception {
		final String newState = "newState";
		final UserRepresentation newAuthor = UserRepresentationTestUtils.createUser();
		final Date newTimestamp = new Date(3000);
		manager.setState(newState, newAuthor, newTimestamp);

		final ModelState<String> currentState = manager.getCurrentState();
		assertEquals(newState, currentState.getValue());
		assertEquals(newAuthor, currentState.getAuthor());
		assertEquals(newTimestamp, currentState.getTimestamp());
	}

	@Test
	public void currentStateOnlyChangesWhenNewStateIsSet() throws Exception {
		final ModelState<String> newModelState = ModelState.create("newState", UserRepresentationTestUtils.createUser(), new Date(3000));
		manager.setState(newModelState);

		for (int i = 0; i < 13; i++) {
			assertEquals(newModelState, manager.getCurrentState());
		}

		final ModelState<String> secondState = ModelState.create("secondState", initialAuthor, new Date(4000));
		manager.setState(secondState);
	}

	@Test(expected = IllegalArgumentException.class)
	public void itShouldNotBeAbleToSetAStateWithEarlierTimeStampThanThePreviousState() throws Exception {
		manager.setState("earlierState", initialAuthor, new Date(500));
	}

	@Test
	public void itShouldBeAbleToSetAStateWithSameTimestampThanThePreviousState() throws Exception {
		final String newState = "new State";
		manager.setState(newState, initialAuthor, initialTimestamp);
		assertEquals(newState, manager.getCurrentStateValue());
	}

	@Test
	public void theDurationOfAStateIsTheDifferenceBetweenItsTimestampAndNowWhenThereIsNoNextState() throws Exception {
		final long duration = new Date().getTime() - initialTimestamp.getTime();
		assertEquals(duration, manager.getCurrentStateDuration(), 2);
	}

	@Test
	public void theDurationOfAStateIsTheDifferenceBetweenItsTimestampAndTheNextStateWhenTheStateOcurredOnlyOnce() throws Exception {
		final ModelState<String> secondState = ModelState.create("secondState", initialAuthor, new Date(3500));
		final long duration = secondState.getTimestamp().getTime() - initialTimestamp.getTime();

		manager.setState(secondState);
		assertEquals(duration, manager.getDurationOfState(initialStateValue));
	}

	@Test
	public void theDurationOfAStateIsTheSumOfTheTimestampDifferences() throws Exception {
		final String trackedStateValue = "trackedState";
		manager.setState(ModelState.create(trackedStateValue, initialAuthor, new Date(3500)));
		manager.setState(ModelState.create("anotherState", initialAuthor, new Date(4000)));

		manager.setState(ModelState.create(trackedStateValue, initialAuthor, new Date(5000)));
		manager.setState(ModelState.create("oneMoreAnotherState", initialAuthor, new Date(6500)));

		assertEquals(2000, manager.getDurationOfState(trackedStateValue));
	}

	@Test
	public void ifTheGivenStateIsTheLastStateTheDurationIsIncreasedByTheDifferenceBetweenTheLastStateTimestampAndCurrentTime() throws Exception {
		final String trackedStateValue = "trackedState";
		manager.setState(ModelState.create(trackedStateValue, initialAuthor, new Date(3500)));
		manager.setState(ModelState.create("anotherState", initialAuthor, new Date(4000)));

		final Date lastTimestamp = new Date(5000);
		manager.setState(ModelState.create(trackedStateValue, initialAuthor, lastTimestamp));

		final long duration = 500 + new Date().getTime() - lastTimestamp.getTime();
		assertEquals(duration, manager.getDurationOfState(trackedStateValue), 2);
	}

	@Test
	public void theLastOccurenceOfAStateThatWasNotSetIsNull() throws Exception {
		final String stateValue = "inextantState";
		assertNull(manager.getLastOccurenceOf(stateValue));

		manager.setState(ModelState.create("state1", initialAuthor, new Date(3512313)));
		manager.setState(ModelState.create("state2", initialAuthor, new Date(3512314)));

		assertNull(manager.getLastOccurenceOf(stateValue));
	}

	@Test
	public void theLastOccurenceOfAStateIsTheCurrentStateIfTheGivenStateIsTheLastOne() throws Exception {
		final String stateValue = "state";
		final UserRepresentation author = UserRepresentationTestUtils.createUser();
		final Date timestamp = new Date(1231231);
		manager.setState(stateValue, author, timestamp);

		final ModelState<String> lastOccurenceOfTheGivenState = manager.getLastOccurenceOf(stateValue);
		assertEquals(manager.getCurrentState(), lastOccurenceOfTheGivenState);
		assertEquals(stateValue, lastOccurenceOfTheGivenState.getValue());
		assertEquals(author, lastOccurenceOfTheGivenState.getAuthor());
		assertEquals(timestamp, lastOccurenceOfTheGivenState.getTimestamp());
	}

	@Test
	public void shouldBeAbleToGetTheLastOccurenceOfAGivenStateValue() throws Exception {
		final String stateValue = "stattttteeeees";
		final ModelState<String> state = ModelState.create(stateValue, initialAuthor, new Date(3512312));
		manager.setState(state);

		manager.setState(ModelState.create("otherState", initialAuthor, new Date(3512313)));
		manager.setState(ModelState.create("anotherOne", initialAuthor, new Date(3512314)));

		assertEquals(state, manager.getLastOccurenceOf(stateValue));
	}

	@Test
	public void shouldBeAbleToRetrieveTheInitialStateEvenAfterMultipleStateChanges() throws Exception {
		final ModelState<String> initialState = ModelState.create(initialStateValue, initialAuthor, initialTimestamp);

		assertEquals(initialState, manager.getCurrentState());
		assertEquals(initialState, manager.getInitialState());

		manager.setState(ModelState.create("otherState", initialAuthor, new Date(3512313)));
		manager.setState(ModelState.create("anotherOne", initialAuthor, new Date(3512314)));

		assertEquals(initialState, manager.getInitialState());
	}

	@Test
	public void stateSetInTheSameTimeShouldBeOverrided() throws Exception {
		final String overridedStateValue = "overridedState";
		manager.setState(ModelState.create(overridedStateValue, initialAuthor, new Date(3512314)));
		manager.setState(ModelState.create("anotherOne", initialAuthor, new Date(3512314)));

		assertNull(manager.getFirstOccurenceOf(overridedStateValue));
	}

	@Test
	public void theNewStateShouldNotBeAddedWhenTheSecondLastStateIsTheSameAsTheNewStateWhenItOverridesTheLastState() throws Exception {
		final String repeatedStateValue = "anotherOne";
		final long firstOccurence = 3512310;

		manager.setState(ModelState.create(repeatedStateValue, initialAuthor, new Date(firstOccurence)));
		manager.setState(ModelState.create("overridedState", initialAuthor, new Date(3512314)));
		manager.setState(ModelState.create(repeatedStateValue, initialAuthor, new Date(3512314)));

		assertEquals(firstOccurence, manager.getLastOccurenceOf(repeatedStateValue).getTimestamp().getTime());
	}
}
