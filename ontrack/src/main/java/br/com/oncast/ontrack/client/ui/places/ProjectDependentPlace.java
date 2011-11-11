package br.com.oncast.ontrack.client.ui.places;

import com.google.gwt.place.shared.Place;

public abstract class ProjectDependentPlace extends Place {

	public abstract long getRequestedProjectId();
}