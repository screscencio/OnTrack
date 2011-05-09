package br.com.oncast.ontrack.client.ui.place;

import br.com.oncast.ontrack.client.ui.place.scope.ScopePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ ScopePlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
