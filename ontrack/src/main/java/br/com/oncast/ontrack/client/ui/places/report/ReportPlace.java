package br.com.oncast.ontrack.client.ui.places.report;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlaceTokenBuilder;
import br.com.oncast.ontrack.shared.places.PlaceTokenParser;
import br.com.oncast.ontrack.shared.places.PlaceTokenType;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ReportPlace extends ProjectDependentPlace implements OpenInNewWindowPlace {

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

		@Override
		public ReportPlace getPlace(final String token) {
			final PlaceTokenParser parser = new PlaceTokenParser(token);

			final UUID projectId = parser.get(PlaceTokenType.PROJECT, UUID.INVALID_UUID);
			final UUID releaseId = parser.get(PlaceTokenType.RELEASE, UUID.INVALID_UUID);

			return new ReportPlace(projectId, releaseId);
		}

		@Override
		public String getToken(final ReportPlace place) {
			return place.getToken();
		}
	}

	public UUID getRequestedReleaseId() {
		return releaseId;
	}

	@Override
	public String getPlacePrefix() {
		return PlacesPrefixes.REPORT;
	}

	@Override
	public String getToken() {
		return new PlaceTokenBuilder()
				.add(PlaceTokenType.PROJECT, getRequestedProjectId())
				.add(PlaceTokenType.RELEASE, getRequestedReleaseId())
				.getToken();
	}
}
