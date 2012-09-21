package br.com.oncast.ontrack.shared.exceptions;

public enum ExceptionMessageCodes {
	HANDLE_INCOMMING_ACTION {
		@Override
		public String getTranslatedMessage(final ExceptionMessages messages) {
			return messages.handleIncommingAction();
		}
	};

	public abstract String getTranslatedMessage(final ExceptionMessages messages);
}
