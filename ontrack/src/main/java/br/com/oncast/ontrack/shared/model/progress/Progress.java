// DECISION Lobo, does not agree with the approach taken to keep the dates relative to progress changes, having BurnUp specific logic invading the model, as
// seen below. This implementation will be kept for now anyway according to business decisions and Rodrigo's opinion on how this should be implemented.

package br.com.oncast.ontrack.shared.model.progress;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityByGetter;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class Progress implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ProgressState {
		NOT_STARTED("Not started") {
			@Override
			protected boolean matches(final String description) {
				final String[] acceptableDescriptions = { "not started", "notstarted", "not_started", "ns", "n", "" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}

			@Override
			protected void handleStateChange(final Progress progress) {
				progress.resetEndDate();
			}
		},
		UNDER_WORK("Under work") {
			@Override
			protected boolean matches(final String description) {
				return (!NOT_STARTED.matches(description) && !DONE.matches(description));
			}

			@Override
			protected void handleStateChange(final Progress progress) {
				progress.start();
				progress.resetEndDate();
			}
		},
		DONE("Done") {
			@Override
			protected boolean matches(final String description) {
				final String[] acceptableDescriptions = { "done", "dn", "d" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}

			@Override
			protected void handleStateChange(final Progress progress) {
				progress.start();
				progress.end();
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

		protected abstract boolean matches(String description);

		protected abstract void handleStateChange(Progress progress);

	};

	@DeepEqualityByGetter
	private String description;

	@IgnoredByDeepEquality
	private ProgressState state = ProgressState.NOT_STARTED;

	@IgnoredByDeepEquality
	private WorkingDay startDate;

	@IgnoredByDeepEquality
	private WorkingDay endDate;

	@IgnoredByDeepEquality
	private Date lastUpdateTimestamp;

	public Progress() {
		setDescription("");
	}

	public String getDescription() {
		return (!hasDeclared() || state == ProgressState.UNDER_WORK) ? description : state.getDescription();
	}

	public void setDescription(final String newProgressDescription) {
		setDescription(newProgressDescription, null);
	}

	public void setDescription(String newProgressDescription, Date timestamp) {
		if (timestamp == null) timestamp = new Date();
		if (newProgressDescription == null) newProgressDescription = "";

		description = newProgressDescription;
		lastUpdateTimestamp = timestamp;
		setState(ProgressState.getStateForDescription(description));
		ProgressDefinitionManager.getInstance().onProgressDefinition(getDescription());
	}

	public ProgressState getState() {
		return state;
	}

	public boolean hasDeclared() {
		return !description.isEmpty();
	}

	public boolean isDone() {
		return state == ProgressState.DONE;
	}

	public WorkingDay getStartDay() {
		return startDate != null ? startDate.copy() : null;
	}

	public WorkingDay getEndDay() {
		return endDate != null ? endDate.copy() : null;
	}

	void setState(final ProgressState newState) {
		state = newState;
		state.handleStateChange(this);
	}

	private void start() {
		if (startDate == null) startDate = WorkingDayFactory.create(lastUpdateTimestamp);
	}

	private void end() {
		endDate = WorkingDayFactory.create(lastUpdateTimestamp);
	}

	private void resetEndDate() {
		endDate = null;
	}
}
