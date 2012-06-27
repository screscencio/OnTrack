package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.place.shared.Place;

public abstract class ProjectDependentPlace extends Place {

	public abstract UUID getRequestedProjectId();

	@Override
	public boolean equals(final Object obj) {
		return false;
	}
}