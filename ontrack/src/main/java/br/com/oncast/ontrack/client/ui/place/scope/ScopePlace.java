package br.com.oncast.ontrack.client.ui.place.scope;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ScopePlace extends Place {

	private final String token;

	public ScopePlace(final String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<ScopePlace> {

		@Override
		public ScopePlace getPlace(final String token) {
			return new ScopePlace(token);
		}

		@Override
		public String getToken(final ScopePlace place) {
			return place.getToken();
		}
	}
}
