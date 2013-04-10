package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.services.feedback.ProjectCreationQuotaRequisitionCallback;
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
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionWidget extends Composite implements HasCloseHandlers<ProjectSelectionWidget>, PopupAware {

	private static final ProjectSelectionWidgetMessages messages = GWT.create(ProjectSelectionWidgetMessages.class);

	private static final int FILTRABLE_MENU_MAX_HEIGHT = 172;

	private static final int FILTRABLE_MENU_MAX_WIDTH = 425;

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static ProjectSelectionWidgetUiBinder uiBinder = GWT.create(ProjectSelectionWidgetUiBinder.class);

	interface ProjectSelectionWidgetUiBinder extends UiBinder<Widget, ProjectSelectionWidget> {}

	@UiField(provided = true)
	protected FiltrableCommandMenu filtrableMenu;

	@UiField
	protected SimplePanel loadingPanel;

	@UiField
	protected FlowPanel rootPanel;

	private final ProjectListChangeListener projectListChangeListener;

	public static ProjectSelectionWidget forProjectSwitchingMenu() {
		return new ProjectSelectionWidget(createForProjectSwitchingMenu());
	}

	public ProjectSelectionWidget() {
		this(createDefaultFiltrableCommandMenu());
	}

	public ProjectSelectionWidget(final FiltrableCommandMenu filtrableMenu) {
		this.filtrableMenu = filtrableMenu;

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

			@Override
			public void onProjectNameUpdate(final ProjectRepresentation projectRepresentation) {}

		};
		registerProjectListChangeListener();
		registerCloseHandler();
	}

	private static FiltrableCommandMenu createForProjectSwitchingMenu() {
		return configureFiltrableMenu(new FiltrableCommandMenu(createCustomItemFactory(), 325, FILTRABLE_MENU_MAX_HEIGHT, true));
	}

	private static FiltrableCommandMenu createDefaultFiltrableCommandMenu() {
		return configureFiltrableMenu(new FiltrableCommandMenu(createCustomItemFactory(), FILTRABLE_MENU_MAX_WIDTH, FILTRABLE_MENU_MAX_HEIGHT)
				.setCloseOnEscape(false));

	}

	private static FiltrableCommandMenu configureFiltrableMenu(final FiltrableCommandMenu filtrableMenu) {
		return filtrableMenu.setAlwaysShowMenu(false)
				.setHelpText(messages.selectionHelpText())
				.setLargePadding();
	}

	private static CustomCommandMenuItemFactory createCustomItemFactory() {
		return new CustomCommandMenuItemFactory() {
			private CommandMenuItem requisitionItem;

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				final int projectCreationQuota = SERVICE_PROVIDER.authentication().getProjectCreationQuota();
				if (projectCreationQuota > 0) return createProjectCreationItem(inputText, projectCreationQuota);
				else return getProjectCreationQuotaRequisitionItem();
			}

			private CommandMenuItem createProjectCreationItem(final String inputText, final int projectCreationQuota) {
				return new SimpleCommandMenuItem(messages.createNewProject(inputText, projectCreationQuota), inputText,
						new Command() {
							@Override
							public void execute() {
								createNewProject(inputText);
							}
						}).setGrowAnimation(false);
			}

			private CommandMenuItem getProjectCreationQuotaRequisitionItem() {
				return requisitionItem == null ? requisitionItem = createProjectCreationQuotaRequisitionItem() : requisitionItem;
			}

			private CommandMenuItem createProjectCreationQuotaRequisitionItem() {
				return new TextAndImageCommandMenuItem("icon-ban-circle", messages.askForMoreProjects(),
						new Command() {
							@Override
							public void execute() {
								requestProjectCreationQuota();
							}
						}).setGrowAnimation(false);
			}

			@Override
			public String getNoItemText() {
				return messages.noProjects();
			}

			@Override
			public boolean shouldPrioritizeCustomItem() {
				return false;
			}

		};
	}

	public ProjectSelectionWidget setMinimalist(final boolean isMinimalist) {
		filtrableMenu.setAlwaysShowMenu(!isMinimalist);
		return this;
	}

	public void setMinimalist(final String isMinimalist) {
		filtrableMenu.setAlwaysShowMenu(!Boolean.valueOf(isMinimalist));
	}

	public ProjectSelectionWidget setCloseOnEscape(final boolean bool) {
		filtrableMenu.setCloseOnEscape(bool);
		return this;
	}

	public void setCloseOnEscape(final String bool) {
		filtrableMenu.setCloseOnEscape(bool);
	}

	protected void hideLoadingIndicator() {
		loadingPanel.setVisible(false);
		filtrableMenu.setVisible(true);
		filtrableMenu.focus();
		filtrableMenu.selectFirstItem();
	}

	protected void showLoadingIndicator() {
		loadingPanel.setVisible(true);
		filtrableMenu.setVisible(false);
	}

	private void registerCloseHandler() {
		filtrableMenu.addCloseHandler(new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				CloseEvent.fire(ProjectSelectionWidget.this, ProjectSelectionWidget.this);
			}
		});
	}

	private void updateProjectMenuItens(final Set<ProjectRepresentation> projectRepresentations) {
		filtrableMenu.setItems(buildUpdateProjectCommandMenuItemList(projectRepresentations));
		filtrableMenu.focus();
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
		SERVICE_PROVIDER.placeController().goTo(projectPlanningPlace);
	}

	private static void createNewProject(final String inputText) {
		final ProjectCreationPlace projectCreationPlace = new ProjectCreationPlace(inputText);
		SERVICE_PROVIDER.placeController().goTo(projectCreationPlace);
	}

	@UiHandler("filtrableMenu")
	protected void onAttachOrDetach(final AttachEvent event) {
		if (event.isAttached()) registerProjectListChangeListener();
		else unregisterProjectListChangeListener();
	}

	private void registerProjectListChangeListener() {
		SERVICE_PROVIDER.projectRepresentationProvider().registerProjectListChangeListener(projectListChangeListener);
	}

	private void unregisterProjectListChangeListener() {
		SERVICE_PROVIDER.projectRepresentationProvider().unregisterProjectListChangeListener(projectListChangeListener);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ProjectSelectionWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		filtrableMenu.show();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		filtrableMenu.hide();
	}

	public void focus() {
		filtrableMenu.focus();
	}

	protected static void requestProjectCreationQuota() {
		SERVICE_PROVIDER.alerting().showInfo(messages.processingProjectQuotaRequest());
		ClientServices.get().feedback().requestProjectCreationQuota(new ProjectCreationQuotaRequisitionCallback() {
			@Override
			public void onRequestSentSucessfully() {
				SERVICE_PROVIDER.alerting().showSuccess(messages.projectQuotaRequestSent());
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				SERVICE_PROVIDER.alerting().showWarning(caught.getLocalizedMessage());
			}
		});
	}
}
