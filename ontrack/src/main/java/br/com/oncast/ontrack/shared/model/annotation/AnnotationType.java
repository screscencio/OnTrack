package br.com.oncast.ontrack.shared.model.annotation;

public enum AnnotationType {
	OPEN_IMPEDIMENT,
	SIMPLE,
	SOLVED_IMPEDIMENT,
	COMMENT {
		@Override
		public boolean acceptsAttachment() {
			return false;
		}

		@Override
		public boolean acceptsComments() {
			return false;
		};
	};

	public boolean acceptsAttachment() {
		return true;
	}

	public boolean acceptsComments() {
		return true;
	}
}
