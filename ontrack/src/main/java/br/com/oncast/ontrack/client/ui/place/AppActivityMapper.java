package br.com.oncast.ontrack.client.ui.place;

import br.com.oncast.ontrack.client.ui.place.scope.ScopeActivity;
import br.com.oncast.ontrack.client.ui.place.scope.ScopePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof ScopePlace) return new ScopeActivity();
		return null;
	}

}
