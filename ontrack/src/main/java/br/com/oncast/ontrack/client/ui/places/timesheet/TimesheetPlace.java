package br.com.oncast.ontrack.client.ui.places.timesheet;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlaceTokenBuilder;
import br.com.oncast.ontrack.shared.places.PlaceTokenParser;
import br.com.oncast.ontrack.shared.places.PlaceTokenType;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TimesheetPlace extends ProjectDependentPlace {

	private final UUID projectId;
	private final UUID releaseId;

	public TimesheetPlace(final UUID projectId, final UUID releaseId) {
		this.projectId = projectId;
		this.releaseId = releaseId;
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	public UUID getReleaseId() {
		return releaseId;
	}

	@Prefix(PlacesPrefixes.TIMESHEET)
	public static class Tokenizer implements PlaceTokenizer<TimesheetPlace> {

		@Override
		public TimesheetPlace getPlace(final String token) {
			final PlaceTokenParser parser = new PlaceTokenParser(token);

			final UUID projectId = parser.get(PlaceTokenType.PROJECT, UUID.INVALID_UUID);
			final UUID releaseId = parser.get(PlaceTokenType.RELEASE);

			return new TimesheetPlace(projectId, releaseId);
		}

		@Override
		public String getToken(final TimesheetPlace place) {
			return new PlaceTokenBuilder()
					.add(PlaceTokenType.PROJECT, place.getRequestedProjectId())
					.add(PlaceTokenType.RELEASE, place.getReleaseId())
					.getToken();
		}
	}
}
