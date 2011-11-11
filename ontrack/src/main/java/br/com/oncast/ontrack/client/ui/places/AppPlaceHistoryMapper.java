package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlanningPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {}
