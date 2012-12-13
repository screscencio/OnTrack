package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlanningPlace extends ProjectDependentPlace {

	private final UUID projectId;
	private final UUID selectedScopeId;

	public PlanningPlace(final UUID projectId, final UUID selectedScopeId) {
		this.projectId = projectId;
		this.selectedScopeId = selectedScopeId;
	}

	public PlanningPlace(final UUID projectId) {
		this(projectId, null);
	}

	public PlanningPlace(final ProjectRepresentation projectRepresentation) {
		this(projectRepresentation.getId());
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	public UUID getSelectedScopeId() {
		return selectedScopeId;
	}

	@Prefix(PlacesPrefixes.PLANNING)
	public static class Tokenizer implements PlaceTokenizer<PlanningPlace> {

		@Override
		public PlanningPlace getPlace(final String token) {
			UUID projectId;
			UUID selectedScopeId;
			final String[] parameters = token.split(PlacesPrefixes.ARGUMENT_SEPARATOR);
			try {
				projectId = new UUID(parameters[0]);
				selectedScopeId = parameters.length > 1 ? new UUID(parameters[1]) : null;
			}
			catch (final Exception e) {
				projectId = UUID.INVALID_UUID;
				selectedScopeId = null;
			}
			return new PlanningPlace(projectId, selectedScopeId);
		}

		@Override
		public String getToken(final PlanningPlace place) {
			return place.getRequestedProjectId().toString();
		}
	}
}
