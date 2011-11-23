package br.com.oncast.ontrack.client.ui.places.projectSelection;

import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ProjectSelectionWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionPanel extends Composite implements ProjectSelectionView {

	@UiField
	protected ProjectSelectionWidget selectionProject;

	@UiFactory
	protected ProjectSelectionWidget createProjectSwitchCommandMenu() {
		return new ProjectSelectionWidget(false);
	}

	private static ProjectSelectionPanelUiBinder uiBinder = GWT.create(ProjectSelectionPanelUiBinder.class);

	interface ProjectSelectionPanelUiBinder extends UiBinder<Widget, ProjectSelectionPanel> {}

	public ProjectSelectionPanel(final ProjectSelectionActivity projectSelectionActivity) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
