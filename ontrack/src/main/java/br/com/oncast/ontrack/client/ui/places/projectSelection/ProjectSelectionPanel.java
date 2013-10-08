package br.com.oncast.ontrack.client.ui.places.projectSelection;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectSelectionWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationWidgetContainer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionPanel extends Composite implements ProjectSelectionView {

	private static ProjectSelectionPanelUiBinder uiBinder = GWT.create(ProjectSelectionPanelUiBinder.class);

	interface ProjectSelectionPanelUiBinder extends UiBinder<Widget, ProjectSelectionPanel> {}

	@UiField
	protected FocusPanel rootPanel;

	@UiField
	protected ApplicationWidgetContainer container;

	@UiField
	protected ProjectSelectionWidget selectionProject;

	@UiFactory
	protected ProjectSelectionWidget createProjectSwitchCommandMenu() {
		return new ProjectSelectionWidget();
	}

	public ProjectSelectionPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("rootPanel")
	protected void handleMouseUpEvent(final MouseUpEvent event) {
		selectionProject.focus();
	}

	@UiHandler("logoutLabel")
	protected void onLogoutClick(final ClickEvent event) {
		ClientServices.get().authentication().logout();
	}

	@Override
	public void focus() {
		selectionProject.focus();
	}

	@Override
	public Panel getAlertingContainer() {
		return container.getAlertingContainer();
	}
}
