package br.com.oncast.ontrack.client.ui.places.planning;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class PlannnigPlace extends Place {

	private final String token;

	public PlannnigPlace(final String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<PlannnigPlace> {

		@Override
		public PlannnigPlace getPlace(final String token) {
			return new PlannnigPlace(token);
		}

		@Override
		public String getToken(final PlannnigPlace place) {
			return place.getToken();
		}
	}
}
