package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.admin.OnTrackStatisticsPlace;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlanningPlace.Tokenizer.class, ProjectSelectionPlace.Tokenizer.class, ProgressPlace.Tokenizer.class, DetailPlace.Tokenizer.class,
		OrganizationPlace.Tokenizer.class, OnTrackStatisticsPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {}
