package br.com.oncast.ontrack.client.ui.places.migration;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MigrationActivity extends AbstractActivity {

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(new MigrationPanel());
	}

}
