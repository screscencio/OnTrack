package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.ApplicationEntryPoint;

import com.google.gwt.place.shared.Place;

public class LoginPlace extends Place {

	private final Place destinationPlace;

	public LoginPlace() {
		this.destinationPlace = ApplicationEntryPoint.DEFAULT_APP_PLACE;
	}

	public LoginPlace(final Place destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	public Place getDestinationPlace() {
		return destinationPlace;
	}
}
