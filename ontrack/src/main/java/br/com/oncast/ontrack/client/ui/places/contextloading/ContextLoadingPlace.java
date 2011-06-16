package br.com.oncast.ontrack.client.ui.places.contextloading;

import com.google.gwt.place.shared.Place;

public class ContextLoadingPlace extends Place {

	private final Place destinationPlace;

	public ContextLoadingPlace(final Place destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	public Place getDestinationPlace() {
		return destinationPlace;
	}
}
