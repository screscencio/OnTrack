package br.com.oncast.ontrack.client.ui.components.organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.ContainerAlignment;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.Orientation;
import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.widgets.ProjectSummaryWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
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
	ModelWidgetContainer<ProjectRepresentation, ProjectSummaryWidget> projects;

	@UiField
	ApplicationMenuAndWidgetContainer rootPanel;

	@UiField
	HTMLPanel container;

	private Set<HandlerRegistration> handlerRegistrations;

	private final UUID selectedProjectId;

	@UiFactory
	ApplicationMenuAndWidgetContainer createMenu() {
		return new ApplicationMenuAndWidgetContainer(false);
	}

	public OrganizationPanel(final UUID selectedProjectId) {
		this.selectedProjectId = selectedProjectId;
		handlerRegistrations = new HashSet<HandlerRegistration>();
		projects = new ModelWidgetContainer<ProjectRepresentation, ProjectSummaryWidget>(
				new ModelWidgetFactory<ProjectRepresentation, ProjectSummaryWidget>() {
					@Override
					public ProjectSummaryWidget createWidget(final ProjectRepresentation project) {
						return new ProjectSummaryWidget(project);
					}
				});

		initWidget(uiBinder.createAndBindUi(this));

		registerProjectListChangeListener();
	}

	private void registerProjectListChangeListener() {
		if (!handlerRegistrations.isEmpty()) return;

		handlerRegistrations.add(ClientServices.get().projectRepresentationProvider()
				.registerProjectListChangeListener(new ProjectListChangeListener() {
					private boolean shouldSelectAProject = true;

					@Override
					public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
						final ArrayList<ProjectRepresentation> modelBeanList = new ArrayList<ProjectRepresentation>(projectRepresentations);
						Collections.sort(modelBeanList);
						projects.update(modelBeanList);

						if (!shouldSelectAProject || modelBeanList.isEmpty()) {
							shouldSelectAProject = modelBeanList.isEmpty();
							return;
						}

						shouldSelectAProject = false;
						ensureVisible(getSelectedProjectWidget(modelBeanList));
					}

					@Override
					public void onProjectListAvailabilityChange(final boolean availability) {}

					@Override
					public void onProjectNameUpdate(final ProjectRepresentation projectRepresentation) {
						final ProjectSummaryWidget widget = projects.getWidgetFor(projectRepresentation);
						if (widget == null) return;

						widget.setProjectRepresentation(projectRepresentation);
					}
				}));
	}

	@Override
	protected void onLoad() {
		registerProjectListChangeListener();
	}

	@Override
	protected void onUnload() {
		removeHandlers();
	}

	private void removeHandlers() {
		for (final HandlerRegistration r : handlerRegistrations) {
			r.removeHandler();
		}
		handlerRegistrations.clear();
	}

	public ApplicationMenu getApplicationMenu() {
		return rootPanel.getMenu();
	}

	public Widget getAlertingContainer() {
		return rootPanel.getContentPanelWidget();
	}

	private ProjectSummaryWidget getSelectedProjectWidget(final ArrayList<ProjectRepresentation> modelBeanList) {
		ProjectSummaryWidget widget = projects.getWidgetFor(modelBeanList.get(0));
		for (final ProjectRepresentation project : modelBeanList) {
			if (project.getId().equals(selectedProjectId)) {
				widget = projects.getWidgetFor(project);
				break;
			}
		}
		return widget;
	}

	private void ensureVisible(final ProjectSummaryWidget widget) {
		widget.setContainerState(true);
		WidgetVisibilityEnsurer.ensureVisible(
				widget.getElement(),
				container.getElement(),
				Orientation.VERTICAL,
				ContainerAlignment.BEGIN, 10);
	}

}
