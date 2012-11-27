// DECISION Lobo, does not agree with the approach taken to keep the dates relative to progress changes, having BurnUp specific logic invading the model, as
// seen below. This implementation will be kept for now anyway according to business decisions and Rodrigo's opinion on how this should be implemented.

package br.com.oncast.ontrack.shared.model.progress;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.ModelState;
import br.com.oncast.ontrack.shared.model.ModelStateManager;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityByGetter;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class Progress implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NOT_STARTED_NAME = "Not Started";

	public enum ProgressState {
		NOT_STARTED("") {
			@Override
			public boolean matches(final String description) {
				final String[] acceptableDescriptions = { "not started", "notstarted", "not_started", "ns", "n", "" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}
		},
		UNDER_WORK("Under work") {
			@Override
			public boolean matches(final String description) {
				return (!NOT_STARTED.matches(description) && !DONE.matches(description));
			}
		},
		DONE("Done") {
			@Override
			public boolean matches(final String description) {
				final String[] acceptableDescriptions = { "done", "dn", "d" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}
		};

		private final String description;

		public static ProgressState getStateForDescription(final String description) {
			for (final ProgressState item : ProgressState.values())
				if (item != UNDER_WORK && item.matches(description)) return item;
			return UNDER_WORK;
		}

		private ProgressState(final String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return getDescription();
		}

		public abstract boolean matches(String description);

	};

	@DeepEqualityByGetter
	private String description;

	@IgnoredByDeepEquality
	private ModelStateManager<ProgressState> stateManager;

	// IMPORTANT used by serialization
	protected Progress() {}

	public Progress(final User author, final Date timestamp) {
		stateManager = new ModelStateManager<ProgressState>(ProgressState.NOT_STARTED, author, timestamp);
		description = "";
	}

	public String getDescription() {
		final ProgressState currentState = stateManager.getCurrentStateValue();
		return (!hasDeclared() || currentState == ProgressState.UNDER_WORK) ? description : currentState.getDescription();
	}

	public String getDeclaredDescription() {
		return description;
	}

	public void setDescription(String newProgressDescription, final User author, final Date timestamp) {
		if (newProgressDescription == null) newProgressDescription = "";

		description = newProgressDescription;
		updateStateToDeclared(author, timestamp);
		ProgressDefinitionManager.getInstance().onProgressDefinition(getDescription());
	}

	public ProgressState getState() {
		return stateManager.getCurrentStateValue();
	}

	public boolean hasDeclared() {
		return !description.isEmpty();
	}

	public boolean isDone() {
		return stateManager.getCurrentStateValue() == ProgressState.DONE;
	}

	public boolean isUnderWork() {
		return stateManager.getCurrentStateValue() == ProgressState.UNDER_WORK;
	}

	void setState(final ProgressState newState, final User author, final Date timestamp) {
		stateManager.setState(newState, author, timestamp);
	}

	public WorkingDay getEndDay() {
		if (!isDone()) return null;

		return WorkingDayFactory.create(stateManager.getLastOccurenceOf(ProgressState.DONE).getTimestamp());
	}

	public WorkingDay getStartDay() {
		ModelState<ProgressState> startDayState = stateManager.getFirstOccurenceOf(ProgressState.UNDER_WORK);
		if (startDayState == null) startDayState = stateManager.getFirstOccurenceOf(ProgressState.DONE);
		return startDayState == null ? null : WorkingDayFactory.create(startDayState.getTimestamp());
	}

	void updateStateToDeclared(final User author, final Date timestamp) {
		setState(ProgressState.getStateForDescription(description), author, timestamp);
	}

}
