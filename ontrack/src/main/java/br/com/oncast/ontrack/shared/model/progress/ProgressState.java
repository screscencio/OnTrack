package br.com.oncast.ontrack.shared.model.progress;

public enum ProgressState {
	NOT_STARTED("", "Not Started") {
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

	private final String label;

	public static String getLabelForDescription(final String description) {
		return NOT_STARTED.matches(description) ? NOT_STARTED.getLabel() : description;
	}

	public static ProgressState getStateForDescription(final String description) {
		for (final ProgressState item : ProgressState.values())
			if (item != UNDER_WORK && item.matches(description)) return item;
		return UNDER_WORK;
	}

	private ProgressState(final String description) {
		this(description, description);
	}

	private ProgressState(final String description, final String label) {
		this.description = description;
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public abstract boolean matches(String description);

}