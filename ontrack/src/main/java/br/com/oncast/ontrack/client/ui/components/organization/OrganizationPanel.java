package br.com.oncast.ontrack.client.ui.components.organization;

import java.util.ArrayList;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.widgets.ProjectSummaryWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationPanel extends Composite {

	private static OrganizationPanelUiBinder uiBinder = GWT.create(OrganizationPanelUiBinder.class);

	interface OrganizationPanelUiBinder extends UiBinder<Widget, OrganizationPanel> {}

	@UiField(provided = true)
	ModelWidgetContainer<ProjectRepresentation, ProjectSummaryWidget> projects;

	@UiField
	ApplicationMenuAndWidgetContainer rootPanel;

	@UiFactory
	ApplicationMenuAndWidgetContainer createMenu() {
		return new ApplicationMenuAndWidgetContainer(false);
	}

	public OrganizationPanel() {
		projects = new ModelWidgetContainer<ProjectRepresentation, ProjectSummaryWidget>(
				new ModelWidgetFactory<ProjectRepresentation, ProjectSummaryWidget>() {

					@Override
					public ProjectSummaryWidget createWidget(final ProjectRepresentation project) {
						return new ProjectSummaryWidget(project);
					}
				});

		initWidget(uiBinder.createAndBindUi(this));

		ClientServiceProvider.getInstance().getProjectRepresentationProvider().registerProjectListChangeListener(new ProjectListChangeListener() {
			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
				projects.update(new ArrayList<ProjectRepresentation>(projectRepresentations));
			}

			@Override
			public void onProjectListAvailabilityChange(final boolean availability) {}
		});
	}

	public ApplicationMenu getApplicationMenu() {
		return rootPanel.getMenu();
	}

	public Widget getAlertingContainer() {
		return rootPanel.getContentPanelWidget();
	}

}
