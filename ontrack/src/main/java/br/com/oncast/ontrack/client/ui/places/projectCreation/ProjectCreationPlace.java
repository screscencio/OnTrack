package br.com.oncast.ontrack.client.ui.places.projectCreation;

import com.google.gwt.place.shared.Place;

public class ProjectCreationPlace extends Place {

	private final String projectName;

	public ProjectCreationPlace(final String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}
}
