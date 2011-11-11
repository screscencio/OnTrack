package br.com.oncast.ontrack.client.ui.places.contextloading;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;

import com.google.gwt.place.shared.Place;

public class ContextLoadingPlace extends Place {

	private final ProjectDependentPlace destinationPlace;

	public ContextLoadingPlace(final ProjectDependentPlace destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	public ProjectDependentPlace getDestinationPlace() {
		return destinationPlace;
	}
}
