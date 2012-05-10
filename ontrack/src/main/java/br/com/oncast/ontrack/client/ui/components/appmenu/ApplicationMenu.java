package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationSubmenu;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.InvitationWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.PasswordChangeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ProjectMenuWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected HTMLPanel applicationMenuPanel;

	@UiField
	protected ApplicationMenuItem projectMenuItem;

	@UiField
	protected ApplicationMenuItem memberMenuItem;

	@UiField
	protected ApplicationMenuItem userMenuItem;

	@UiField
	protected HTMLPanel itemContainer;

	@UiField
	protected Image backButton;

	public ApplicationMenu() {
		initWidget(uiBinder.createAndBindUi(this));

		hideBackButton();

		createProjectMenu();

		createMemberMenu();

		createUserMenu();
	}

	private void hideBackButton() {
		setBackButtonVisibility(false);
	}

	public void setBackButtonVisibility(final boolean shouldBeVisible) {
		backButton.setVisible(shouldBeVisible);
	}

	@UiHandler("backButton")
	protected void onBackButtonClick(final ClickEvent event) {
		final long projectId = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrent().getId();
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(new PlanningPlace(projectId));
	}

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

	private void createProjectMenu() {
		final PopupConfig config = PopupConfig.configPopup().popup(new ProjectMenuWidget())
				.alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(projectMenuItem, HorizontalAlignment.CENTER));
		projectMenuItem.setPopupConfig(config);
	}

	private void createMemberMenu() {
		final PopupConfig invitePopup = PopupConfig.configPopup().popup(new InvitationWidget())
				.alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(memberMenuItem, HorizontalAlignment.CENTER));
		memberMenuItem.setPopupConfig(invitePopup);
	}

	private void createUserMenu() {
		final ApplicationSubmenu userMenu = new ApplicationSubmenu();

		final PopupConfig popupPassChange = PopupConfig.configPopup().popup(new PasswordChangeWidget()).alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(userMenuItem, HorizontalAlignment.CENTER))
				.setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
		final PopupConfig popup = PopupConfig.configPopup().popup(userMenu).alignBelow(applicationMenuPanel, 1)
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(userMenuItem, HorizontalAlignment.CENTER));

		userMenu.addItem("Change Password", new Command() {
			@Override
			public void execute() {
				popupPassChange.pop();
			}
		});

		final User currentUser = SERVICE_PROVIDER.getAuthenticationService().getCurrentUser();
		final String logoutText = "Logout" + ((currentUser != null) ? ", " + currentUser.getEmail() : "");
		userMenu.addItem(logoutText, new Command() {
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
				ClientServiceProvider.getInstance().getClientNotificationService().showError("Logout failed.");
			}
		});
	}
}