package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationSubmenu;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.MembersWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationListWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.PasswordChangeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ProjectMenuWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static final ApplicationMenuMessages messages = GWT.create(ApplicationMenuMessages.class);

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected HTMLPanel applicationMenuPanel;

	@UiField
	protected ApplicationMenuItem projectMenuItem;

	@UiField
	protected NotificationMenuItem notificationMenuItem;

	@UiField
	protected ApplicationMenuItem memberMenuItem;

	@UiField
	protected ApplicationMenuItem userMenuItem;

	@UiField
	protected HTMLPanel itemContainer;

	@UiField
	protected Image backButton;

	public ApplicationMenu() {
		this(true);
	}

	public ApplicationMenu(final boolean enableProjectDependantMenus) {
		initWidget(uiBinder.createAndBindUi(this));

		hideBackButton();

		if (enableProjectDependantMenus) createProjectMenu();
		projectMenuItem.setVisible(enableProjectDependantMenus);

		createNotificationMenu();

		if (enableProjectDependantMenus) createMemberMenu();
		memberMenuItem.setVisible(enableProjectDependantMenus);

		createUserMenu();

		backButton.addClickHandler(enableProjectDependantMenus ? new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final UUID projectId = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrent().getId();
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(new PlanningPlace(projectId));
			}
		} : new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(new ProjectSelectionPlace());
			}
		});

		backButton.setTitle(enableProjectDependantMenus ? messages.backToProject() : messages.backToProjectSelection());
	}

	private void hideBackButton() {
		setBackButtonVisibility(false);
	}

	public void setBackButtonVisibility(final boolean shouldBeVisible) {
		backButton.setVisible(shouldBeVisible);
	}

	@SuppressWarnings("deprecation")
	public ApplicationMenu addCustomMenuItem(final ApplicationMenuItem menuItem, final Widget widgetToPopup) {
		itemContainer.add(menuItem);
		menuItem.setPopupConfig(PopupConfig.configPopup().popup(widgetToPopup).alignBelow(applicationMenuPanel, 1)
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

	@SuppressWarnings("deprecation")
	private void createProjectMenu() {
		final PopupConfig config = PopupConfig.configPopup().popup(new ProjectMenuWidget())
				.alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(projectMenuItem, HorizontalAlignment.CENTER));
		projectMenuItem.setPopupConfig(config);
	}

	@SuppressWarnings("deprecation")
	private void createNotificationMenu() {
		final PopupConfig notificationPopup = PopupConfig.configPopup().popup(new NotificationListWidget())
				.alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(notificationMenuItem, HorizontalAlignment.CENTER));
		notificationMenuItem.setPopupConfig(notificationPopup);
	}

	@SuppressWarnings("deprecation")
	private void createMemberMenu() {
		final PopupConfig invitePopup = PopupConfig.configPopup().popup(new MembersWidget())
				.alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(memberMenuItem, HorizontalAlignment.CENTER));
		memberMenuItem.setPopupConfig(invitePopup);
	}

	@SuppressWarnings("deprecation")
	private void createUserMenu() {
		final ApplicationSubmenu userMenu = new ApplicationSubmenu();

		final PopupConfig popupPassChange = PopupConfig.configPopup().popup(new PasswordChangeWidget()).alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(userMenuItem, HorizontalAlignment.CENTER))
				.setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
		final PopupConfig popup = PopupConfig.configPopup().popup(userMenu).alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(userMenuItem, HorizontalAlignment.CENTER));

		userMenu.addItem(messages.changePassword(), new Command() {
			@Override
			public void execute() {
				popupPassChange.pop();
			}
		});

		final User currentUser = SERVICE_PROVIDER.getAuthenticationService().getCurrentUser();
		userMenu.addItem(currentUser == null ? messages.logout() : messages.logout(currentUser.getEmail()), new Command() {
			@Override
			public void execute() {
				logUserOut();
			}
		});

		userMenuItem.setPopupConfig(popup);
	}

	private void logUserOut() {
		SERVICE_PROVIDER.getAuthenticationService().logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Threat this error properly. Maybe even call the ErrorService.
				ClientServiceProvider.getInstance().getClientAlertingService().showError(messages.logoutFailed());
			}
		});
	}

	public void openProjectsMenuItem() {
		projectMenuItem.toggleMenu();
	}

	public void openNotificationsMenuItem() {
		notificationMenuItem.openMenu();
	}

	public void openMembersMenuItem() {
		memberMenuItem.toggleMenu();
	}

	public void openUserMenuItem() {
		userMenuItem.toggleMenu();
	}

}