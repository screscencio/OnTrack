package br.com.oncast.ontrack.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

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

	public ModelState<T> getCurrentState() {
		return statesList.get(statesList.size() - 1);
	}

	public T getCurrentStateValue() {
		return getCurrentState().getValue();
	}

	public void setState(final T newState, final UserRepresentation newAuthor, final Date newTimestamp) {
		setState(ModelState.create(newState, newAuthor, newTimestamp));
	}

	public void setState(final ModelState<T> newModelState) {
		final ModelState<T> currentModelState = getCurrentState();
		final long timeDifference = newModelState.getTimestamp().getTime() - currentModelState.getTimestamp().getTime();

		if (timeDifference < 0) throw new IllegalArgumentException("It's not possible to set a state that happened before the current state");

		if (timeDifference == 0) this.statesList.remove(currentModelState);
		if (!statesList.isEmpty() && getCurrentStateValue() == newModelState.getValue()) return;

		this.statesList.add(newModelState);
	}

	public long getCurrentStateDuration() {
		return new Date().getTime() - getCurrentState().getTimestamp().getTime();
	}

	public long getDurationOfState(final T stateValue) {
		long duration = 0;

		for (int i = 0; i < statesList.size() - 1; i++) {
			final ModelState<T> state = statesList.get(i);
			if (isTheRequiredState(state, stateValue)) {
				duration += getDurationBetween(state, statesList.get(i + 1));
			}
		}
		if (isTheRequiredState(getCurrentState(), stateValue)) duration += getCurrentStateDuration();

		return duration;
	}

	private boolean isTheRequiredState(final ModelState<T> state, final T stateValue) {
		return state.getValue().equals(stateValue);
	}

	private long getDurationBetween(final ModelState<T> previousState, final ModelState<T> nextState) {
		return nextState.getTimestamp().getTime() - previousState.getTimestamp().getTime();
	}

	public ModelState<T> getLastOccurenceOf(final T stateValue) {
		for (int i = statesList.size() - 1; i >= 0; i--) {
			final ModelState<T> state = statesList.get(i);
			if (isTheRequiredState(state, stateValue)) return state;
		}
		return null;
	}

	public ModelState<T> getInitialState() {
		return statesList.get(0);
	}

	public ModelState<T> getFirstOccurenceOf(final T stateValue) {
		for (int i = 0; i < statesList.size(); i++) {
			final ModelState<T> state = statesList.get(i);
			if (isTheRequiredState(state, stateValue)) return state;
		}
		return null;
	}

	@Override
	public Iterator<ModelState<T>> iterator() {
		return statesList.iterator();
	}

}
