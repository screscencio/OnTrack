package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof PlannnigPlace) return new PlanningActivity();
		return null;
	}

}
