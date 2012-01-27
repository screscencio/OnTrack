package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.InvitationWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.PasswordChangeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ProjectSelectionWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected MenuBar projectMenuBar;

	@UiField
	protected MenuBar userMenuBar;

	@UiField
	protected HTMLPanel customItemContainer;

	private MenuBar projectMenu;

	private MenuBar userMenu;

	public ApplicationMenu() {
		initWidget(uiBinder.createAndBindUi(this));

		createProjectMenu();

		createUserMenu();
	}

	public void setCustomItem(final IsWidget widget) {
		customItemContainer.add(widget);
	}

	private void logUserOut() {
		SERVICE_PROVIDER.getAuthenticationService().logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Threat this error properly. Maybe even call the ErrorService.
				ClientNotificationService.showError("It was not possible to log the user out properly.");
			}
		});
	}

	private void createUserMenu() {
		userMenu = new MenuBar(true);
		userMenu.addStyleDependentName("topMenu");
		final MenuItem item = userMenuBar.addItem("User", userMenu);

		final PopupConfig popup = PopupConfig.configPopup().popup(new PasswordChangeWidget()).alignRight(item).alignBelow(item, 4);
		userMenu.addItem("Change Password", new Command() {
			@Override
			public void execute() {
				popup.pop();
			}
		});

		userMenu.addItem("Logout", new Command() {
			@Override
			public void execute() {
				logUserOut();
			}
		});
	}

	private void createProjectMenu() {
		projectMenu = new MenuBar(true);
		projectMenu.addStyleDependentName("topMenu");
		final MenuItem item = projectMenuBar.addItem("Project", projectMenu);

		final PopupConfig projectSelectionPopup = PopupConfig.configPopup().popup(new ProjectSelectionWidget()).alignRight(item)
				.alignBelow(item);
		projectMenu.addItem("Switch project", new Command() {
			@Override
			public void execute() {
				projectSelectionPopup.pop();
			}
		});

		final PopupConfig invitePopup = PopupConfig.configPopup().popup(new InvitationWidget()).alignRight(item).alignBelow(item, 4);
		projectMenu.addItem("Invite", new Command() {
			@Override
			public void execute() {
				invitePopup.pop();
			}
		});
	}
}