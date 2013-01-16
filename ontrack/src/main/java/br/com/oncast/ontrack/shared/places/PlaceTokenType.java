package br.com.oncast.ontrack.shared.places;

public enum PlaceTokenType {
	PROJECT(""),
	SCOPE(":"),
	RELEASE("@"),
	TAG("&");

	private final String identifier;

	private PlaceTokenType(final String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}
}