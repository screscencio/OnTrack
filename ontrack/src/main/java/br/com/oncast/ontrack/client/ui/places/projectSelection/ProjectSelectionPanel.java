package br.com.oncast.ontrack.client.ui.places.projectSelection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionPanel extends Composite implements ProjectSelectionView {

	private static ProjectSelectionPanelUiBinder uiBinder = GWT.create(ProjectSelectionPanelUiBinder.class);

	interface ProjectSelectionPanelUiBinder extends UiBinder<Widget, ProjectSelectionPanel> {}

	public ProjectSelectionPanel(final ProjectSelectionActivity projectSelectionActivity) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
