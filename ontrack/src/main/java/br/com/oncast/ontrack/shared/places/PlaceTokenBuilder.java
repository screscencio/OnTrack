package br.com.oncast.ontrack.shared.places;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class PlaceTokenBuilder {

	private final StringBuilder builder;

	public PlaceTokenBuilder() {
		this.builder = new StringBuilder();
	}

	public void add(final PlaceTokenType type, final HasUUID hasUUID) {
		add(type, hasUUID.getId());
	}

	public void add(final PlaceTokenType type, final UUID id) {
		builder.append(type.getIdentifier());
		builder.append(id.toString());
	}

	public String getToken() {
		return builder.toString();
	}

}
