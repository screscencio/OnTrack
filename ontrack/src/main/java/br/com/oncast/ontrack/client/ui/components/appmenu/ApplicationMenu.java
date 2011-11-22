package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.ui.components.appmenu.interaction.PlanningAuthenticationRequestHandler;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ChangePasswordForm;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

// TODO +++This widget and its subwidgets should be refactored, dividing logic and ui in better ways.
public class ApplicationMenu extends Composite {

	private static ApplicationMenuWidgetUiBinder uiBinder = GWT.create(ApplicationMenuWidgetUiBinder.class);

	interface ApplicationMenuWidgetUiBinder extends UiBinder<Widget, ApplicationMenu> {}

	@UiField
	protected Label changePasswordLabel;

	@UiField
	protected Label logoutLabel;

	private PlanningAuthenticationRequestHandler authenticationRequestHandler;

	public ApplicationMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		PopupConfig.link(changePasswordLabel).popup(new ChangePasswordForm()).alignPopupRight(changePasswordLabel).alignBelow(this);
	}

	@UiHandler("logoutLabel")
	protected void logoutLabelOnClick(final ClickEvent event) {
		authenticationRequestHandler.logoutUser();
	}

	public void setAuthenticationRequestHandler(final PlanningAuthenticationRequestHandler authenticationRequestHandler) {
		this.authenticationRequestHandler = authenticationRequestHandler;
	}
}