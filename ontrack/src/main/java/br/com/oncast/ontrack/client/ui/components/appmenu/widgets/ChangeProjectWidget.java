package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectCreationListener;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChangeProjectWidget extends Composite {

	private static ChangeProjectWidgetUiBinder uiBinder = GWT.create(ChangeProjectWidgetUiBinder.class);

	interface ChangeProjectWidgetUiBinder extends UiBinder<Widget, ChangeProjectWidget> {}

	@UiField
	protected Label projectSwitchingMenuButton;

	@UiField
	protected FiltrableCommandMenu projectSwitchingMenu;

	private final ProjectListChangeListener projectListChangeListener;

	@UiFactory
	protected FiltrableCommandMenu createProjectSwitchCommandMenu() {
		return new FiltrableCommandMenu(new CustomCommandMenuItemFactory() {

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				return new CommandMenuItem("Create new project '" + inputText + "'", new Command() {

					@Override
					public void execute() {
						createNewProject(inputText);
					}
				});
			}
		}, 700, 400);
	}

	public ChangeProjectWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		this.projectListChangeListener = new ProjectListChangeListener() {

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
				updateProjectMenuItens(projectRepresentations);
			}
		};

		projectSwitchingMenu.hide();
		registerProjectListChangeListener();
	}

	@UiHandler("projectSwitchingMenuButton")
	protected void onClick(final ClickEvent e) {
		showChangeProjectMenu();
	}

	private void showChangeProjectMenu() {
		projectSwitchingMenu.show(projectSwitchingMenuButton);
	}

	private void updateProjectMenuItens(final Set<ProjectRepresentation> projectRepresentations) {
		projectSwitchingMenu.setItems(buildUpdateProjectCommandMenuItemList(projectRepresentations));
	}

	private List<CommandMenuItem> buildUpdateProjectCommandMenuItemList(final Set<ProjectRepresentation> projectRepresentations) {
		final List<CommandMenuItem> projects = new ArrayList<CommandMenuItem>();

		for (final ProjectRepresentation representation : projectRepresentations)
			projects.add(createProjectMenuItem(representation));

		return projects;
	}

	private CommandMenuItem createProjectMenuItem(final ProjectRepresentation projectRepresentation) {
		return new CommandMenuItem(projectRepresentation.getName(), new Command() {

			@Override
			public void execute() {
				openProject(projectRepresentation);
			}
		});
	}

	private void openProject(final ProjectRepresentation projectRepresentation) {
		final PlanningPlace projectPlanningPlace = new PlanningPlace(projectRepresentation);
		ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(projectPlanningPlace);
	}

	private void createNewProject(final String inputText) {
		// FIXME Show loading feedback
		ClientServiceProvider.getInstance().getProjectRepresentationProvider()
				.createNewProject(inputText, new ProjectCreationListener() {

					@Override
					public void onProjectCreated(final ProjectRepresentation projectRepresentation) {
						// FIXME Hide loading feedback
						openProject(projectRepresentation);
					}

					@Override
					public void onProjectCreationFailure() {
						// FIXME Hide loading feedback
						// FIXME Treat failure
					}

					@Override
					public void onUnexpectedFailure() {
						// FIXME Hide loading feedback
						// FIXME Treat failure
					}
				});
	}

	private void registerProjectListChangeListener() {
		ClientServiceProvider.getInstance().getProjectRepresentationProvider().registerProjectListChangeListener(projectListChangeListener);
	}
}
