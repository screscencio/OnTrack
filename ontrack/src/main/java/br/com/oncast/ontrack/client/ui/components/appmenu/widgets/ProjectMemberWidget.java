package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectAuthorizationCallback;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget.UserUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMemberWidget extends Composite implements ModelWidget<UserRepresentation> {

	private static final ProjectMemeberWidgetMessages messages = GWT.create(ProjectMemeberWidgetMessages.class);

	private static ProjectMemberWidgetUiBinder uiBinder = GWT.create(ProjectMemberWidgetUiBinder.class);

	interface ProjectMemberWidgetUiBinder extends UiBinder<Widget, ProjectMemberWidget> {}

	@UiField
	Label userNameLabel;

	@UiField
	HorizontalPanel container;

	@UiField(provided = true)
	UserWidget userWidget;

	@UiField
	FocusPanel removeUser;

	@UiField
	Label confirmRemotion;

	private final UserRepresentation user;

	private String userName;

	public ProjectMemberWidget(final UserRepresentation user) {
		this.user = user;
		userWidget = new UserWidget(user, new UserUpdateListener() {

			@Override
			public void onUserUpdate(final User user) {
				userName = user.getName();
				if (userNameLabel != null) update();
			}
		});
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		removeUser.setVisible(false);
		confirmRemotion.setVisible(false);
	}

	@UiHandler("focusPanel")
	protected void onMouseOver(final MouseOverEvent e) {
		removeUser.setVisible(true);
	}

	@UiHandler("focusPanel")
	protected void onMouseOut(final MouseOutEvent e) {
		removeUser.setVisible(false);
	}

	@UiHandler("confirmRemotion")
	protected void onConfirmRemotionClick(final ClickEvent e) {
		ClientServiceProvider.getInstance().getProjectRepresentationProvider().unauthorizeUser(user, new ProjectAuthorizationCallback() {
			@Override
			public void onSuccess() {
				ClientServiceProvider.getInstance().getClientAlertingService()
						.showSuccess(messages.userRemoved());
			}

			@Override
			public void onFailure(final Throwable caught) {
				final boolean knownError = caught instanceof UnableToRemoveAuthorizationException;
				if (!knownError) caught.printStackTrace();

				ClientServiceProvider.getInstance().getClientAlertingService()
						.showError(knownError ? messages.cantRemoveAdmin() : messages.userRemoveFailed());
			}
		});
	}

	@UiHandler("removeUser")
	protected void onRemoveClick(final ClickEvent e) {
		confirmRemotion.setVisible(true);
	}

	@Override
	public boolean update() {
		userNameLabel.setText(userName);
		return false;
	}

	@Override
	public UserRepresentation getModelObject() {
		return user;
	}
}
