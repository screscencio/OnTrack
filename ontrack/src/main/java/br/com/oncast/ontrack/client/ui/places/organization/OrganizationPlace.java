package br.com.oncast.ontrack.client.ui.places.organization;

import br.com.oncast.ontrack.client.ui.places.RestorablePlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class OrganizationPlace extends Place implements RestorablePlace {

	private final UUID projectId;

	public OrganizationPlace(final UUID projectId) {
		this.projectId = projectId;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.ORGANIZATION)
	public static class Tokenizer implements PlaceTokenizer<OrganizationPlace> {

		@Override
		public OrganizationPlace getPlace(final String token) {
			UUID projectId;
			final String[] parameters = token.split(PlacesPrefixes.ARGUMENT_SEPARATOR);
			try {
				projectId = parameters.length > 0 ? new UUID(parameters[1]) : null;
			}
			catch (final Exception e) {
				projectId = UUID.INVALID_UUID;
			}

			return new OrganizationPlace(projectId);
		}

		@Override
		public String getToken(final OrganizationPlace place) {
			return "";
		}
	}

	public UUID getProject() {
		return projectId;
	}
}
