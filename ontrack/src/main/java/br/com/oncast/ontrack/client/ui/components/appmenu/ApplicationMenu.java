package br.com.oncast.ontrack.client.ui.components.appmenu;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.PasswordChangeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ProjectSelectionWidget;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected Label projectSwitchingMenuLabel;

	@UiField
	protected Label changePasswordLabel;

	@UiField
	protected Label logoutLabel;

	public ApplicationMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		configPopup().link(changePasswordLabel).popup(new PasswordChangeWidget()).alignRight(changePasswordLabel).alignBelow(this);
		configPopup().link(projectSwitchingMenuLabel).popup(new ProjectSelectionWidget(true)).alignRight(projectSwitchingMenuLabel).alignBelow(this);
	}

	@UiHandler("logoutLabel")
	protected void logoutLabelOnClick(final ClickEvent event) {
		logUserOut();
	}

	// // XXX Auth; The Authentication service could allow observers to know when a user logged in or out.
	private void logUserOut() {
		SERVICE_PROVIDER.getAuthenticationService().logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {
				// TODO Launch a login place instead of reloading the page.
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(new LoginPlace());
			}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Threat this error properly. Maybe even call the ErrorService.
				Window.alert("It was not possible to log the user out properly.");
			}
		});
	}
}