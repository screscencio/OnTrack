package br.com.oncast.ontrack.client.ui.places.migration;

import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class MigrationPlace extends Place {

	@Prefix(PlacesPrefixes.MIGRATION)
	public static class Tokenizer implements PlaceTokenizer<MigrationPlace> {

		@Override
		public MigrationPlace getPlace(final String token) {
			return new MigrationPlace();
		}

		@Override
		public String getToken(final MigrationPlace place) {
			return "";
		}
	}

}
