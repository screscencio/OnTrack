package br.com.oncast.ontrack.client.ui.places.details;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class DetailPlace extends ProjectDependentPlace {

	private final UUID projectId;
	private final UUID subjectId;
	private final Place destinationPlace;
	private final boolean hasLoadedPlace;

	public DetailPlace(final UUID projectId, final UUID subjectId, final Place destinationPlace, final boolean hasLoadedPlace) {
		this.projectId = projectId;
		this.subjectId = subjectId;
		this.destinationPlace = destinationPlace;
		this.hasLoadedPlace = hasLoadedPlace;
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

	@Prefix(PlacesPrefixes.DETAIL)
	public static class Tokenizer implements PlaceTokenizer<DetailPlace> {

		@Override
		public DetailPlace getPlace(final String token) {
			UUID projectId;
			UUID subjectId;
			final String[] parameters = token.split(PlacesPrefixes.ARGUMENT_SEPARATOR);
			try {
				projectId = new UUID(parameters[0]);
				subjectId = new UUID(parameters[1]);
			}
			catch (final Exception e) {
				projectId = UUID.INVALID_UUID;
				subjectId = UUID.INVALID_UUID;
			}
			return new DetailPlace(projectId, subjectId, new PlanningPlace(projectId), false);
		}

		@Override
		public String getToken(final DetailPlace place) {
			return place.getRequestedProjectId() + PlacesPrefixes.ARGUMENT_SEPARATOR + place.getSubjectId();
		}
	}

	public UUID getSubjectId() {
		return subjectId;
	}

	public Place getDestinationPlace() {
		return destinationPlace;
	}

	public boolean hasLoadedPlace() {
		return hasLoadedPlace;
	}

}
