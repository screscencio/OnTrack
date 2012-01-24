package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ProgressPlace extends ProjectDependentPlace {

	private final long projectId;
	private final UUID releaseId;

	public ProgressPlace(final long projectId, final UUID releaseId) {
		this.projectId = projectId;
		this.releaseId = releaseId;
	}

	public ProgressPlace(final ProjectRepresentation projectRepresentation, final UUID releaseId) {
		this.projectId = projectRepresentation.getId();
		this.releaseId = releaseId;
	}

	@Override
	public long getRequestedProjectId() {
		return projectId;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	public static class Tokenizer implements PlaceTokenizer<ProgressPlace> {

		private static final String SEPARATOR = ":";

		@Override
		public ProgressPlace getPlace(final String token) {
			long projectId;
			UUID releaseId;
			final String[] parameters = token.split(SEPARATOR);
			try {
				projectId = Long.parseLong(parameters[0]);
				releaseId = new UUID(parameters[1]);
			}
			catch (final Exception e) {
				projectId = 0;
				releaseId = new UUID("0");
			}
			return new ProgressPlace(projectId, releaseId);
		}

		@Override
		public String getToken(final ProgressPlace place) {
			return place.getRequestedProjectId() + SEPARATOR + place.getRequestedReleaseId();
		}
	}

	public UUID getRequestedReleaseId() {
		return releaseId;
	}
}
