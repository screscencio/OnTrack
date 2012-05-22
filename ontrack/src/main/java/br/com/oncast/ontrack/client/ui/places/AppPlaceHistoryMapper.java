package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.migration.MigrationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlanningPlace.Tokenizer.class, ProjectSelectionPlace.Tokenizer.class, ProgressPlace.Tokenizer.class, MigrationPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {}
