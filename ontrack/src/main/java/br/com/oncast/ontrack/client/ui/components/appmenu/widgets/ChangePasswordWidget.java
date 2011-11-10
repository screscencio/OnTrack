package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.components.appmenu.interaction.PlanningAuthenticationRequestHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChangePasswordWidget extends Composite {

	private static ChangePasswordWidgetUiBinder uiBinder = GWT.create(ChangePasswordWidgetUiBinder.class);

	interface ChangePasswordWidgetUiBinder extends UiBinder<Widget, ChangePasswordWidget> {}

	@UiField
	protected Label changePasswordLabel;

	@UiField
	protected ChangePasswordForm changePasswordForm;

	public ChangePasswordWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		changePasswordForm.hide();
	}

	@UiHandler("changePasswordLabel")
	protected void onClick(final ClickEvent e) {
		changePasswordForm.show();
		changePasswordForm.focus();
	}

	public void setAuthenticationRequestHandler(final PlanningAuthenticationRequestHandler authenticationRequestHandler) {
		changePasswordForm.setAuthenticationRequestHandler(authenticationRequestHandler);
	}
}
