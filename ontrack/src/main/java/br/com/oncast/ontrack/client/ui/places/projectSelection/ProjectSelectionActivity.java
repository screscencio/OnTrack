package br.com.oncast.ontrack.client.ui.places.projectSelection;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ProjectSelectionActivity extends AbstractActivity {

	private final ProjectSelectionView view;

	public ProjectSelectionActivity() {
		this.view = new ProjectSelectionPanel(this);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(view.asWidget());
	}

}
