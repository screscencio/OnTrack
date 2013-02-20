package br.com.oncast.ontrack.client.ui.places.timesheet;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlaceTokenBuilder;
import br.com.oncast.ontrack.shared.places.PlaceTokenParser;
import br.com.oncast.ontrack.shared.places.PlaceTokenType;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TimesheetPlace extends ProjectDependentPlace {

	private final UUID projectId;
	private final UUID releaseId;
	private final Place destinationPlace;
	private final boolean hasLoadedPlace;

	public TimesheetPlace(final UUID projectId, final UUID releaseId, final Place destinationPlace, final boolean hasLoadedPlace) {
		this.projectId = projectId;
		this.releaseId = releaseId;
		this.destinationPlace = destinationPlace;
		this.hasLoadedPlace = hasLoadedPlace;
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	public UUID getReleaseId() {
		return releaseId;
	}

	public Place getDestinationPlace() {
		return destinationPlace;
	}

	public boolean hasLoadedPlace() {
		return hasLoadedPlace;
	}

	@Prefix(PlacesPrefixes.TIMESHEET)
	public static class Tokenizer implements PlaceTokenizer<TimesheetPlace> {

		@Override
		public TimesheetPlace getPlace(final String token) {
			final PlaceTokenParser parser = new PlaceTokenParser(token);

			final UUID projectId = parser.get(PlaceTokenType.PROJECT, UUID.INVALID_UUID);
			final UUID releaseId = parser.get(PlaceTokenType.RELEASE);

			return new TimesheetPlace(projectId, releaseId, new PlanningPlace(projectId), false);
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
