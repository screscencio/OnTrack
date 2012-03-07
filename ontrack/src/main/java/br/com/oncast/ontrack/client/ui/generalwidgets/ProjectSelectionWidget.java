package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionWidget extends Composite implements HasCloseHandlers<ProjectSelectionWidget>, PopupAware {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ProjectSelectionWidgetUiBinder uiBinder = GWT.create(ProjectSelectionWidgetUiBinder.class);

	interface ProjectSelectionWidgetUiBinder extends UiBinder<Widget, ProjectSelectionWidget> {}

	@UiField
	protected FiltrableCommandMenu projectSwitchingMenu;

	@UiField
	protected SimplePanel loadingPanel;

	@UiField
	protected FlowPanel rootPanel;

	private final ProjectListChangeListener projectListChangeListener;

	@UiFactory
	protected FiltrableCommandMenu createProjectSwitchCommandMenu() {
		return new FiltrableCommandMenu(new CustomCommandMenuItemFactory() {

			@Override
			public SimpleCommandMenuItem createCustomItem(final String inputText) {
				return new SimpleCommandMenuItem("Create new project '" + inputText + "'", inputText, new Command() {

					@Override
					public void execute() {
						createNewProject(inputText);
					}
				});
			}
		}, 576, 400);
	}

	public ProjectSelectionWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		this.projectListChangeListener = new ProjectListChangeListener() {

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
				updateProjectMenuItens(projectRepresentations);
			}

			@Override
			public void onProjectListAvailabilityChange(final boolean projectListAvailable) {
				if (projectListAvailable) hideLoadingIndicator();
				else showLoadingIndicator();
			}
		};
		registerProjectListChangeListener();
		registerCloseHandler();
	}

	public ProjectSelectionWidget setMinimalist(final boolean isMinimalist) {
		projectSwitchingMenu.setAlwaysShowMenu(!isMinimalist);
		return this;
	}

	public void setMinimalist(final String isMinimalist) {
		projectSwitchingMenu.setAlwaysShowMenu(!Boolean.valueOf(isMinimalist));
	}

	public ProjectSelectionWidget setCloseOnEscape(final boolean bool) {
		projectSwitchingMenu.setCloseOnEscape(bool);
		return this;
	}

	public void setCloseOnEscape(final String bool) {
		projectSwitchingMenu.setCloseOnEscape(bool);
	}

	protected void hideLoadingIndicator() {
		loadingPanel.setVisible(false);
		projectSwitchingMenu.setVisible(true);
		projectSwitchingMenu.focus();
		projectSwitchingMenu.selectFirstItem();
	}

	protected void showLoadingIndicator() {
		loadingPanel.setVisible(true);
		projectSwitchingMenu.setVisible(false);
	}

	private void registerCloseHandler() {
		projectSwitchingMenu.addCloseHandler(new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				hide();
			}
		});
	}

	private void updateProjectMenuItens(final Set<ProjectRepresentation> projectRepresentations) {
		projectSwitchingMenu.setItens(buildUpdateProjectCommandMenuItemList(projectRepresentations));
		projectSwitchingMenu.focus();
	}

	private List<CommandMenuItem> buildUpdateProjectCommandMenuItemList(final Set<ProjectRepresentation> projectRepresentations) {
		final List<CommandMenuItem> projects = new ArrayList<CommandMenuItem>();

		for (final ProjectRepresentation representation : projectRepresentations)
			projects.add(createProjectMenuItem(representation));

		return projects;
	}

	private SimpleCommandMenuItem createProjectMenuItem(final ProjectRepresentation projectRepresentation) {
		return new SimpleCommandMenuItem(projectRepresentation.getName(), new Command() {

			@Override
			public void execute() {
				openProject(projectRepresentation);
			}
		});
	}

	private void openProject(final ProjectRepresentation projectRepresentation) {
		final PlanningPlace projectPlanningPlace = new PlanningPlace(projectRepresentation);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectPlanningPlace);
	}

	private void createNewProject(final String inputText) {
		final ProjectCreationPlace projectCreationPlace = new ProjectCreationPlace(inputText);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectCreationPlace);
	}

	@UiHandler("projectSwitchingMenu")
	protected void onAttachOrDetach(final AttachEvent event) {
		if (event.isAttached()) registerProjectListChangeListener();
		else unregisterProjectListChangeListener();
	}

	private void registerProjectListChangeListener() {
		SERVICE_PROVIDER.getProjectRepresentationProvider().registerProjectListChangeListener(projectListChangeListener);
	}

	private void unregisterProjectListChangeListener() {
		SERVICE_PROVIDER.getProjectRepresentationProvider().unregisterProjectListChangeListener(projectListChangeListener);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ProjectSelectionWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		rootPanel.setVisible(true);
		projectSwitchingMenu.show();
	}

	@Override
	public void hide() {
		if (!rootPanel.isVisible()) return;

		rootPanel.setVisible(false);
		projectSwitchingMenu.hide();
		CloseEvent.fire(this, this);
	}

	public void focus() {
		projectSwitchingMenu.focus();
	}
}
