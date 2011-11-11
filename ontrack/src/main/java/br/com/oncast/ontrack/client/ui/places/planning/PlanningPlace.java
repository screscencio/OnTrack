package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.place.shared.PlaceTokenizer;

public class PlanningPlace extends ProjectDependentPlace {

	private final long projectId;

	public PlanningPlace(final long id) {
		this.projectId = id;
	}

	public PlanningPlace(final ProjectRepresentation projectRepresentation) {
		this.projectId = projectRepresentation.getId();
	}

	@Override
	public long getRequestedProjectId() {
		return projectId;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	public static class Tokenizer implements PlaceTokenizer<PlanningPlace> {

		@Override
		public PlanningPlace getPlace(final String token) {
			long projectId;
			try {
				projectId = Long.parseLong(token);
			}
			catch (final NumberFormatException e) {
				projectId = 0;
			}
			return new PlanningPlace(projectId);
		}

		@Override
		public String getToken(final PlanningPlace place) {
			return place.getRequestedProjectId() + "";
		}
	}
}
