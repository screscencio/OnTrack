package br.com.oncast.ontrack.client.ui.places.admin;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class OnTrackStatisticsActivity extends AbstractActivity {

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final OnTrackStatisticsPanel view = new OnTrackStatisticsPanel();
		panel.setWidget(view);
	}

}
