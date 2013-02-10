package br.com.oncast.ontrack.client.ui.places.admin;

import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class OnTrackStatisticsPlace extends Place {

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.ADMIN)
	public static class Tokenizer implements PlaceTokenizer<OnTrackStatisticsPlace> {

		@Override
		public OnTrackStatisticsPlace getPlace(final String token) {
			return new OnTrackStatisticsPlace();
		}

		@Override
		public String getToken(final OnTrackStatisticsPlace place) {
			return "";
		}
	}

}
