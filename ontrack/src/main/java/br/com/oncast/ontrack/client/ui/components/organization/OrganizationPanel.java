package br.com.oncast.ontrack.client.ui.components.organization;

import java.util.ArrayList;
import java.util.Set;

import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.ContainerAlignment;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.Orientation;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.organization.AvailableContextsListChangeListener;
import br.com.oncast.ontrack.client.services.organization.OrganizationContextProviderService;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.widgets.ProjectSummaryWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationPanel extends Composite {

	private static OrganizationPanelUiBinder uiBinder = GWT.create(OrganizationPanelUiBinder.class);

	interface OrganizationPanelUiBinder extends UiBinder<Widget, OrganizationPanel> {}

	@UiField(provided = true)
	ModelWidgetContainer<ProjectContext, ProjectSummaryWidget> projects;

	@UiField
	ApplicationMenuAndWidgetContainer rootPanel;

	@UiField
	HTMLPanel container;

	@UiFactory
	ApplicationMenuAndWidgetContainer createMenu() {
		return new ApplicationMenuAndWidgetContainer(false);
	}

	public OrganizationPanel(final UUID selectedProjectId) {
		projects = new ModelWidgetContainer<ProjectContext, ProjectSummaryWidget>(
				new ModelWidgetFactory<ProjectContext, ProjectSummaryWidget>() {

					@Override
					public ProjectSummaryWidget createWidget(final ProjectContext project) {
						return new ProjectSummaryWidget(project);
					}
				});

		initWidget(uiBinder.createAndBindUi(this));

		final OrganizationContextProviderService contextProvider = ClientServiceProvider.getInstance().getOrganizationContextProviderService();
		contextProvider.registerContextsChangeListener(new AvailableContextsListChangeListener() {
			private boolean firstTime = true;

			@Override
			public void onContextListChange(final Set<ProjectContext> availableProjects) {
				final ArrayList<ProjectContext> modelBeanList = new ArrayList<ProjectContext>(availableProjects);
				projects.update(modelBeanList);
				if (!firstTime) return;

				if (selectedProjectId == null && !modelBeanList.isEmpty()) projects.getWidgetFor(modelBeanList.get(0)).setContainerState(true);
				else for (final ProjectContext projectContext : modelBeanList) {
					if (projectContext.getProjectRepresentation().getId().equals(selectedProjectId)) {
						final ProjectSummaryWidget projectSummary = projects.getWidgetFor(projectContext);
						projectSummary.setContainerState(true);
						WidgetVisibilityEnsurer.ensureVisible(
								projectSummary.getElement(),
								container.getElement(),
								Orientation.VERTICAL,
								ContainerAlignment.BEGIN, 10);
						break;
					}
				}
				firstTime = false;
			}
		});
	}

	public ApplicationMenu getApplicationMenu() {
		return rootPanel.getMenu();
	}

	public Widget getAlertingContainer() {
		return rootPanel.getContentPanelWidget();
	}

}
