package br.com.oncast.ontrack.shared.model.progress;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Progress implements IsSerializable {

	public enum STATUS {
		NOT_STARTED {
			@Override
			protected boolean matches(final String description) {
				final String[] acceptableDescriptions = { "not started", "notstarted", "not_started", "ns", "n" };
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

	};

	private String description;
	private STATUS status;
	private float computedEffort;

	public Progress() {
		reset();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String newProgressDescription) {
		description = newProgressDescription;
		setStatus(description);
	}

	public void reset() {
		description = "";
		status = STATUS.NOT_STARTED;
	}

	public STATUS getStatus() {
		return status;
	}

	private void setStatus(final String description) {
		for (final STATUS item : STATUS.values()) {
			if (item.matches(description)) status = item;
		}
	}

	public boolean hasDeclared() {
		return !description.isEmpty();
	}

	public boolean isDone() {
		return status == STATUS.DONE;
	}

	public float getComputedEffort() {
		return computedEffort;
	}

	public void setComputedEffort(final float computedEffort) {
		this.computedEffort = computedEffort;
	}
}
