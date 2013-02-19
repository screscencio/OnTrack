package br.com.oncast.ontrack.client.ui.places.metrics;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class OnTrackMetricsActivity extends AbstractActivity {

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final OnTrackMetricsPanel view = new OnTrackMetricsPanel();
		panel.setWidget(view);
	}

}
