package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.feedback.ProjectCreationQuotaRequisitionCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSelectionWidget extends Composite implements HasCloseHandlers<ProjectSelectionWidget>, PopupAware {

	private static final ProjectSelectionWidgetMessages messages = GWT.create(ProjectSelectionWidgetMessages.class);

	private static final int FILTRABLE_MENU_MAX_HEIGHT = 172;

	private static final int FILTRABLE_MENU_MAX_WIDTH = 425;

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

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
				final int projectCreationQuota = SERVICE_PROVIDER.getAuthenticationService().getProjectCreationQuota();
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

		projects.add(createOrganizationPlaceMenuItem());

		for (final ProjectRepresentation representation : projectRepresentations)
			projects.add(createProjectMenuItem(representation));

		return projects;
	}

	private CommandMenuItem createOrganizationPlaceMenuItem() {
		final Command cmd = new Command() {
			@Override
			public void execute() {
				final ProjectRepresentationProvider provider = ClientServiceProvider.getInstance().getProjectRepresentationProvider();
				final UUID project = provider.hasAvailableProjectRepresentation() ? provider.getCurrent().getId() : null;
				ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(new OrganizationPlace(project));
			}
		};

		final SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder
				.appendHtmlConstant("<div style=\"color: #cc9900;\">")
				.appendEscaped(messages.allProjectsSummary())
				.appendHtmlConstant("</div>");
		final MenuItem menuItem = new MenuItem(builder.toSafeHtml(), cmd);

		return new CommandMenuItem() {

			@Override
			public String getValue() {
				return "";
			}

			@Override
			public String getText() {
				return "";
			}

			@Override
			public MenuItem getMenuItem() {
				return menuItem;
			}

			@Override
			public boolean executeCommand() {
				cmd.execute();
				return true;
			}

			@Override
			public int compareTo(final CommandMenuItem obj) {
				return Integer.MIN_VALUE;
			}
		};
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

	private static void createNewProject(final String inputText) {
		final ProjectCreationPlace projectCreationPlace = new ProjectCreationPlace(inputText);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectCreationPlace);
	}

	@UiHandler("filtrableMenu")
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
		SERVICE_PROVIDER.getClientAlertingService().showInfo(messages.processingProjectQuotaRequest());
		ClientServiceProvider.getInstance().getFeedbackService().requestProjectCreationQuota(new ProjectCreationQuotaRequisitionCallback() {
			@Override
			public void onRequestSentSucessfully() {
				SERVICE_PROVIDER.getClientAlertingService().showSuccess(messages.projectQuotaRequestSent());
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				SERVICE_PROVIDER.getClientAlertingService().showWarning(caught.getLocalizedMessage());
			}
		});
	}
}
