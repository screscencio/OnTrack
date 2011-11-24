package br.com.oncast.ontrack.client.ui.places.projectSelection;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ProjectSelectionPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<ProjectSelectionPlace> {

		@Override
		public ProjectSelectionPlace getPlace(final String token) {
			return new ProjectSelectionPlace();
		}

		@Override
		public String getToken(final ProjectSelectionPlace place) {
			return "";
		}
	}
}