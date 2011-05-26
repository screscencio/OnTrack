package br.com.oncast.ontrack.client.ui.place;

import br.com.oncast.ontrack.client.ui.place.planning.PlannnigPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlannnigPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
