package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlannnigPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
