package br.com.oncast.ontrack.client.ui.places.report;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ReportPlace extends ProjectDependentPlace {

	private final UUID projectId;
	private final UUID releaseId;

	public ReportPlace(final UUID projectId, final UUID releaseId) {
		this.projectId = projectId;
		this.releaseId = releaseId;
	}

	public ReportPlace(final ProjectRepresentation projectRepresentation, final UUID releaseId) {
		this.projectId = projectRepresentation.getId();
		this.releaseId = releaseId;
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.REPORT)
	public static class Tokenizer implements PlaceTokenizer<ReportPlace> {

		private static final String SEPARATOR = ":";

		@Override
		public ReportPlace getPlace(final String token) {
			UUID projectId;
			UUID releaseId;
			final String[] parameters = token.split(SEPARATOR);
			try {
				projectId = new UUID(parameters[0]);
				releaseId = new UUID(parameters[1]);
			}
			catch (final Exception e) {
				projectId = UUID.INVALID_UUID;
				releaseId = UUID.INVALID_UUID;
			}
			return new ReportPlace(projectId, releaseId);
		}

		@Override
		public String getToken(final ReportPlace place) {
			return place.getRequestedProjectId() + SEPARATOR + place.getRequestedReleaseId();
		}
	}

	public UUID getRequestedReleaseId() {
		return releaseId;
	}
}
