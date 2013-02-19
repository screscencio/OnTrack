package br.com.oncast.ontrack.client.ui.places.metrics;

import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class OnTrackMetricsPlace extends Place {

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.METRICS)
	public static class Tokenizer implements PlaceTokenizer<OnTrackMetricsPlace> {

		@Override
		public OnTrackMetricsPlace getPlace(final String token) {
			return new OnTrackMetricsPlace();
		}

		@Override
		public String getToken(final OnTrackMetricsPlace place) {
			return "";
		}
	}

}
