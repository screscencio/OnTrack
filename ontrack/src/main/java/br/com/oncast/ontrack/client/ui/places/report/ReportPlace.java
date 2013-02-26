package br.com.oncast.ontrack.client.ui.places.report;

import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.places.PlacesPrefixes;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ReportPlace extends ProjectDependentPlace {

	private UUID projectId;

	protected ReportPlace() {}

	public ReportPlace(final UUID projectId) {
		this.projectId = projectId;
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}

	@Prefix(PlacesPrefixes.REPORT)
	public static class Tokenizer implements PlaceTokenizer<ReportPlace> {

		@Override
		public ReportPlace getPlace(final String token) {
			return new ReportPlace(new UUID(token));
		}

		@Override
		public String getToken(final ReportPlace place) {
			return place.getRequestedProjectId().toString();
		}
	}

	@Override
	public UUID getRequestedProjectId() {
		return projectId;
	}

}
