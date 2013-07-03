package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationSubmenu;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.AssignedToMeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationListWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.PasswordChangeWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectSelectionWidget;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static final ApplicationMenuMessages messages = GWT.create(ApplicationMenuMessages.class);

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	interface ApplicationMenuWidgetStyle extends CssResource {
		String logoDisabled();
	}

	@UiField
	protected ApplicationMenuWidgetStyle style;

	@UiField
	protected HTMLPanel applicationMenuPanel;

	@UiField
	protected FocusPanel logo;

	@UiField
	protected ApplicationMenuItem projectMenuItem;

	@UiField
	protected NotificationMenuItem notificationMenuItem;

	@UiField
	protected ApplicationMenuItem userMenuItem;

	@UiField
	protected ApplicationMenuItem assignedToMeMenuItem;

	@UiField
	protected HTMLPanel itemContainer;

	@UiField
	protected Image backButton;

	private HandlerRegistration registration;

	private MenuItem userLogoutMenuItem;

	public ApplicationMenu() {
		this(true);
	}

	public ApplicationMenu(final boolean enableProjectDependantMenus) {
		initWidget(uiBinder.createAndBindUi(this));

		if (getPlaceController().getCurrentPlace() instanceof OrganizationPlace) {
			logo.setStyleName(style.logoDisabled());
			logo.setTitle("");
		}

		hideBackButton();

		if (enableProjectDependantMenus) createProjectMenu();
		projectMenuItem.setVisible(enableProjectDependantMenus);

		createNotificationMenu();

		createUserMenu();

		createAssignedToMeMenu();

		backButton.addClickHandler(enableProjectDependantMenus ? new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final UUID projectId = getCurrentProjectId();
				goTo(new PlanningPlace(projectId));
			}

		} : new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				goTo(new ProjectSelectionPlace());
			}
		});

		backButton.setTitle(enableProjectDependantMenus ? messages.backToProject() : messages.backToProjectSelection());
	}

	@UiHandler("logo")
	void onLogoClick(final ClickEvent e) {
		getPlaceController().goTo(new OrganizationPlace(getCurrentProjectId()));
	}

	private void goTo(final Place place) {
		getPlaceController().goTo(place);
	}

	private ApplicationPlaceController getPlaceController() {
		return SERVICE_PROVIDER.placeController();
	}

	private UUID getCurrentProjectId() {
		return SERVICE_PROVIDER.projectRepresentationProvider().getCurrent().getId();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		registerUserDataUpdateListener();
	}

	private void registerUserDataUpdateListener() {
		if (registration != null) return;

		registration = ClientServices.get().userData().registerListenerForSpecificUser(ClientServices.get().authentication().getCurrentUserId(), new UserSpecificInformationChangeListener() {
			@Override
			public void onInformationChange(final User user) {
				if (userLogoutMenuItem != null) userLogoutMenuItem.setText(messages.logout(user.getName()));
				else unregisterUserDataUpdateListener();
			}
		});
	}

	@Override
	protected void onUnload() {
		unregisterUserDataUpdateListener();
		super.onUnload();
	}

	private void unregisterUserDataUpdateListener() {
		if (registration == null) return;

		registration.removeHandler();
		registration = null;
	}

	private void hideBackButton() {
		setBackButtonVisibility(false);
	}

	public void setBackButtonVisibility(final boolean shouldBeVisible) {
		backButton.setVisible(shouldBeVisible);
	}

	public ApplicationMenu addCustomMenuItem(final ApplicationMenuItem menuItem, final Widget widgetToPopup) {
		itemContainer.add(menuItem);
		menuItem.setPopupConfig(PopupConfig.configPopup().popup(widgetToPopup).alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM, 1))
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(menuItem, HorizontalAlignment.CENTER)));
		return this;
	}

	public ApplicationMenu clearCustomMenuItems() {
		itemContainer.clear();
		return this;
	}

	public void setProjectName(final String name) {
		projectMenuItem.setText(name);
	}

	private void createProjectMenu() {
		final PopupConfig config = PopupConfig.configPopup().popup(ProjectSelectionWidget.forProjectSwitchingMenu())
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM, 1))
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(projectMenuItem, HorizontalAlignment.CENTER));
		projectMenuItem.setPopupConfig(config);
	}

	private void createNotificationMenu() {
		final PopupConfig notificationPopup = PopupConfig.configPopup().popup(new NotificationListWidget())
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM, 1))
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(notificationMenuItem, HorizontalAlignment.RIGHT));
		notificationMenuItem.setPopupConfig(notificationPopup);
	}

	private void createUserMenu() {
		if (registration != null) return;
		final ApplicationSubmenu userMenu = new ApplicationSubmenu();

		final PopupConfig popupPassChange = PopupConfig.configPopup().popup(new PasswordChangeWidget())
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM))
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(userMenuItem, HorizontalAlignment.LEFT)).setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
		final PopupConfig popup = PopupConfig.configPopup().popup(userMenu).alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM))
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(userMenuItem, HorizontalAlignment.LEFT));

		userMenu.addItem(messages.changePassword(), new Command() {
			@Override
			public void execute() {
				popupPassChange.pop();
			}
		});

		userLogoutMenuItem = userMenu.addItem(messages.logout(), new Command() {
			@Override
			public void execute() {
				logUserOut();
			}
		});

		registerUserDataUpdateListener();

		userMenuItem.setPopupConfig(popup);
	}

	private void createAssignedToMeMenu() {
		final PopupConfig config = PopupConfig.configPopup().popup(new AssignedToMeWidget())
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(applicationMenuPanel, VerticalAlignment.BOTTOM, 1))
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(assignedToMeMenuItem, HorizontalAlignment.RIGHT));
		assignedToMeMenuItem.setPopupConfig(config);
	}

	private void logUserOut() {
		SERVICE_PROVIDER.authentication().logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Threat this error properly. Maybe even call the ErrorService.
				ClientServices.get().alerting().showError(messages.logoutFailed());
			}
		});
	}

	public void openProjectsMenuItem() {
		projectMenuItem.toggleMenu();
	}

	public void openNotificationsMenuItem() {
		notificationMenuItem.openMenu();
	}

	public void openUserMenuItem() {
		userMenuItem.toggleMenu();
	}

}