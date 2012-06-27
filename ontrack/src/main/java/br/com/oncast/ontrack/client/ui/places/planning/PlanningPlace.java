package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlanningPlace extends ProjectDependentPlace {

	private final UUID projectId;

	public PlanningPlace(final UUID projectId) {
		this.projectId = projectId;
	}

	public PlanningPlace(final ProjectRepresentation projectRepresentation) {
		this.projectId = projectRepresentation.getId();
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	@Prefix(PlacesPrefixes.PLANNING)
	public static class Tokenizer implements PlaceTokenizer<PlanningPlace> {

		@Override
		public PlanningPlace getPlace(final String token) {
			final UUID projectId = new UUID(token);
			return new PlanningPlace(projectId);
		}

		@Override
		public String getToken(final PlanningPlace place) {
			return place.getRequestedProjectId() + "";
		}
	}
}
