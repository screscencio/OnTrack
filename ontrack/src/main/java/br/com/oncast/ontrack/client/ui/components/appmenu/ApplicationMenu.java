package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.ui.places.planning.authentication.ChangePasswordWidget;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningAuthenticationRequestHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationMenu extends Composite {

	private static ApplicationMenuUiBinder uiBinder = GWT.create(ApplicationMenuUiBinder.class);

	interface ApplicationMenuUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected Label logoutLabel;

	@UiField
	protected ChangePasswordWidget changePasswordWidget;

	private PlanningAuthenticationRequestHandler authenticationRequestHandler;

	public ApplicationMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("logoutLabel")
	protected void logoutLabelOnClick(final ClickEvent event) {
		authenticationRequestHandler.logoutCurrentUser();
	}

	public void setAuthenticationRequestHandler(final PlanningAuthenticationRequestHandler authenticationRequestHandler) {
		this.authenticationRequestHandler = authenticationRequestHandler;
		changePasswordWidget.setAuthenticationRequestHandler(authenticationRequestHandler);
	}
}