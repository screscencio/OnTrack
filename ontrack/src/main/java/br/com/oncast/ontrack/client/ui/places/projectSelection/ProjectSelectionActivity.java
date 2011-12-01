package br.com.oncast.ontrack.client.ui.places.projectSelection;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

// XXX Auth; Add logout button to this activity.
public class ProjectSelectionActivity extends AbstractActivity {

	public ProjectSelectionActivity() {}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ProjectSelectionView view = new ProjectSelectionPanel();
		panel.setWidget(view.asWidget());
		view.focus();
	}
}
