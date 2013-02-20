package br.com.oncast.ontrack.shared.places;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class PlaceTokenBuilder {

	private final StringBuilder builder;

	public PlaceTokenBuilder() {
		this.builder = new StringBuilder();
	}

	public PlaceTokenBuilder add(final PlaceTokenType type, final HasUUID hasUUID) {
		return add(type, hasUUID.getId());
	}

	public PlaceTokenBuilder add(final PlaceTokenType type, final UUID id) {
		builder.append(type.getIdentifier());
		builder.append(id.toString());
		return this;
	}

	public String getToken() {
		return builder.toString();
	}

}
