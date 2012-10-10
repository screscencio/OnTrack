package br.com.oncast.ontrack.client.ui.places.organization;

import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class OrganizationPlace extends Place {

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.ORGANIZATION)
	public static class Tokenizer implements PlaceTokenizer<OrganizationPlace> {

		@Override
		public OrganizationPlace getPlace(final String token) {
			return new OrganizationPlace();
		}

		@Override
		public String getToken(final OrganizationPlace place) {
			return "";
		}
	}
}
