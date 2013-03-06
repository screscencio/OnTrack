package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.RestorablePlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlaceTokenParser;
import br.com.oncast.ontrack.shared.places.PlaceTokenType;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PlanningPlace extends ProjectDependentPlace implements RestorablePlace {

	private final UUID projectId;
	private final UUID selectedScopeId;
	private final UUID tagId;

	public PlanningPlace(final UUID projectId, final UUID selectedScopeId, final UUID tagId) {
		this.projectId = projectId;
		this.selectedScopeId = selectedScopeId;
		this.tagId = tagId;
	}

	public PlanningPlace(final UUID projectId) {
		this(projectId, null, null);
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

	public UUID getTagId() {
		return tagId;
	}

	@Prefix(PlacesPrefixes.PLANNING)
	public static class Tokenizer implements PlaceTokenizer<PlanningPlace> {

		@Override
		public PlanningPlace getPlace(final String token) {
			final PlaceTokenParser parser = new PlaceTokenParser(token);

			final UUID projectId = parser.get(PlaceTokenType.PROJECT, UUID.INVALID_UUID);
			final UUID selectedScopeId = parser.get(PlaceTokenType.SCOPE);
			final UUID tagId = parser.get(PlaceTokenType.TAG);

			return new PlanningPlace(projectId, selectedScopeId, tagId);
		}

		@Override
		public String getToken(final PlanningPlace place) {
			return place.getRequestedProjectId().toString();
		}
	}
}
