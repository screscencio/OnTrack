package br.com.oncast.ontrack.client.ui.places.login;

import static br.com.oncast.ontrack.client.ApplicationEntryPoint.DEFAULT_APP_PLACE;

import com.google.gwt.place.shared.Place;

public class LoginPlace extends Place {

	private Place destinationPlace = DEFAULT_APP_PLACE;

	public LoginPlace() {}

	public LoginPlace(final Place destinationPlace) {
		if (!(destinationPlace instanceof LoginPlace)) this.destinationPlace = destinationPlace;
	}

	public Place getDestinationPlace() {
		return destinationPlace;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}
}
