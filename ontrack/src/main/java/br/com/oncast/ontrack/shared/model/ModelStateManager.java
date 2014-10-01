package br.com.oncast.ontrack.shared.model;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ModelStateManager<T> implements Serializable, Iterable<ModelState<T>> {

	private static final long serialVersionUID = 1L;

	private List<ModelState<T>> statesList;

	protected ModelStateManager() {}

	public ModelStateManager(final T initialStateValue, final UserRepresentation author, final Date initialStateTimestamp) {
		this(ModelState.create(initialStateValue, author, initialStateTimestamp));
	}

	public ModelStateManager(final ModelState<T> initialState) {
		this.statesList = new ArrayList<ModelState<T>>();
		statesList.add(initialState);
	}

	public int getNumberOfStates() {
		return statesList.size();
	}

	public ModelState<T> getCurrentState() {
		return statesList.get(statesList.size() - 1);
	}

	public T getCurrentStateValue() {
		return getCurrentState().getValue();
	}

	public void setState(final T newState, final UserRepresentation newAuthor, final Date newTimestamp) {
		setState(ModelState.create(newState, newAuthor, newTimestamp));
	}

	public void setState(final ModelState<T> newState) {
		final int index = getInsertionIndex(newState.getTimestamp());
		if (index == getNumberOfStates()) statesList.add(newState);
		else {
			final ModelState<T> nextState = getStateAt(index);
			final long timeDifference = newState.getTimestamp().getTime() - nextState.getTimestamp().getTime();

			statesList.add(index, newState);
			if (timeDifference == 0) this.statesList.remove(nextState);
		}
	}

	public ModelState<T> getStateAt(final int index) {
		return statesList.get(index);
	}

	private int getInsertionIndex(final Date timestamp) {
		for (int i = 0; i < statesList.size(); i++) {
			final ModelState<T> state = statesList.get(i);
			if (state.getTimestamp().getTime() >= timestamp.getTime()) return i;
		}
		return statesList.size();
	}

	public long getCurrentStateDuration() {
		return new Date().getTime() - getCurrentState().getTimestamp().getTime();
	}

	public long getDurationOfState(final T stateValue) {
		long duration = 0;

		for (int i = 0; i < statesList.size() - 1; i++) {
			final ModelState<T> nextState = statesList.get(i + 1);
			final ModelState<T> state = statesList.get(i);
			if (hasValue(state, stateValue)) {
				duration += getDurationBetween(state, nextState);
			}
		}

		if (hasValue(getCurrentState(), stateValue)) duration += getCurrentStateDuration();

		return duration;
	}

	private boolean hasValue(final ModelState<T> state, final T stateValue) {
		return state.getValue().equals(stateValue);
	}

	private long getDurationBetween(final ModelState<T> previousState, final ModelState<T> nextState) {
		final Date nextDate = new Date();
		if (nextState.getTimestamp() != null) nextDate.setTime(nextState.getTimestamp().getTime());

		return nextDate.getTime() - previousState.getTimestamp().getTime();
	}

	public ModelState<T> getLastOccurenceOf(final T stateValue) {
		ModelState<T> lastOccurence = null;
		for (int i = statesList.size() - 1; i >= 0; i--) {
			final ModelState<T> state = statesList.get(i);
			if (hasValue(state, stateValue)) lastOccurence = state;
			else if (lastOccurence != null) return lastOccurence;
		}
		return lastOccurence;
	}

	public ModelState<T> getInitialState() {
		return statesList.get(0);
	}

	public ModelState<T> getFirstOccurenceOf(final T stateValue) {
		for (int i = 0; i < statesList.size(); i++) {
			final ModelState<T> state = statesList.get(i);
			if (hasValue(state, stateValue)) return state;
		}
		return null;
	}

	@Override
	public Iterator<ModelState<T>> iterator() {
		return Collections.unmodifiableList(statesList).iterator();
	}

}
