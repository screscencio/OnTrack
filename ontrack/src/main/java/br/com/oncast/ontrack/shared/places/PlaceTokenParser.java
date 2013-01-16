package br.com.oncast.ontrack.shared.places;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.regexp.shared.RegExp;

public class PlaceTokenParser {

	private static final String UUID_REGEX = "[A-Z0-9-]+";

	private final String token;

	public PlaceTokenParser(final String token) {
		this.token = token;
	}

	public UUID get(final PlaceTokenType type) {
		return get(type, null);
	}

	private String extract(final String identifier) {
		return compile(identifier).exec(token).getGroup(0).substring(identifier.length());
	}

	public boolean has(final PlaceTokenType type) {
		return compile(type.getIdentifier()).test(token);
	}

	private RegExp compile(final String identifier) {
		final RegExp reg = RegExp.compile(identifier + UUID_REGEX, "gi");
		return reg;
	}

	public UUID get(final PlaceTokenType type, final UUID defaultValue) {
		return has(type) ? new UUID(extract(type.getIdentifier())) : defaultValue;
	}

}
