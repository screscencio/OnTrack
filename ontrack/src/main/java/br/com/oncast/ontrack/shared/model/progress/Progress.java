package br.com.oncast.ontrack.shared.model.progress;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Progress implements IsSerializable {

	public enum ProgressState {
		NOT_STARTED {
			@Override
			protected boolean matches(final String description) {
				final String[] acceptableDescriptions = { "not started", "notstarted", "not_started", "ns", "n", "" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}
		},
		UNDER_WORK {
			@Override
			protected boolean matches(final String description) {
				return (!NOT_STARTED.matches(description) && !DONE.matches(description));
			}
		},
		DONE {
			@Override
			protected boolean matches(final String description) {
				final String[] acceptableDescriptions = { "done", "dn", "d" };
				for (final String acceptable : acceptableDescriptions) {
					if (acceptable.equalsIgnoreCase(description)) return true;
				}
				return false;
			}
		};

		protected abstract boolean matches(String description);

		public static ProgressState getStateForDescription(final String description) {
			for (final ProgressState item : ProgressState.values())
				if (item != UNDER_WORK && item.matches(description)) return item;
			return UNDER_WORK;
		}

	};

	private String description;
	private ProgressState state;

	public Progress() {
		setDescription("");
	}

	public String getDescription() {
		return (!hasDeclared() || state == ProgressState.UNDER_WORK) ? description : state.toString();
	}

	public void setDescription(final String newProgressDescription) {
		description = newProgressDescription;
		setState(ProgressState.getStateForDescription(description));
	}

	public ProgressState getState() {
		return state;
	}

	private void setState(final ProgressState state) {
		this.state = state;
	}

	public boolean hasDeclared() {
		return !description.isEmpty();
	}

	public boolean isDone() {
		return state == ProgressState.DONE;
	}

	public void markAsCompleted() {
		state = ProgressState.DONE;
	}
}
