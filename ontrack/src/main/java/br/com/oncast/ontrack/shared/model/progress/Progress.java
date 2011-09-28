package br.com.oncast.ontrack.shared.model.progress;

import java.io.Serializable;

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
		},
		UNDER_WORK("Under work") {
			@Override
			protected boolean matches(final String description) {
				return (!NOT_STARTED.matches(description) && !DONE.matches(description));
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
	};

	@DeepEqualityByGetter
	private String description;

	@IgnoredByDeepEquality
	private ProgressState state;

	public Progress() {
		setDescription("");
	}

	public String getDescription() {
		return (!hasDeclared() || state == ProgressState.UNDER_WORK) ? description : state.getDescription();
	}

	public void setDescription(String newProgressDescription) {
		if (newProgressDescription == null) newProgressDescription = "";

		description = newProgressDescription;
		setState(ProgressState.getStateForDescription(description));
	}

	public ProgressState getState() {
		return state;
	}

	void setState(final ProgressState state) {
		this.state = state;
		ProgressDefinitionManager.getInstance().onProgressDefinition(getDescription());
	}

	public boolean hasDeclared() {
		return !description.isEmpty();
	}

	public boolean isDone() {
		return state == ProgressState.DONE;
	}

}
