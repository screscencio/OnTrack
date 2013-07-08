package br.com.oncast.ontrack.client.ui.components.user;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.client.services.user.UserHasGravatarCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareCanInviteAction;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareReadOnlyAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserInformationCard extends Composite implements HasCloseHandlers<UserInformationCard>, PopupAware {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static UserInformationCardUiBinder uiBinder = GWT.create(UserInformationCardUiBinder.class);

	interface UserInformationCardUiBinder extends UiBinder<Widget, UserInformationCard> {}

	private static UserInformationCardMessages messages = GWT.create(UserInformationCardMessages.class);

	interface UserInformationCardStyle extends CssResource {

		String userImageContainerImageColor();

		String userImageContainerLabelColor();
	}

	@UiField
	UserInformationCardStyle style;

	@UiField
	Image author;

	@UiField
	Label userEmail;

	@UiField(provided = true)
	EditableLabel userName;

	@UiField
	FocusPanel container;

	@UiField
	DeckPanel userImageContainer;

	@UiField
	Label userWithoutImage;

	@UiField
	CheckBox readOnlyCheckBox;

	@UiField
	CheckBox canInviteCheckBox;

	private User user;

	private boolean isCurrentUserSuperUser;

	private final UserRepresentation userRepresentation;

	private Set<HandlerRegistration> registrations;

	public UserInformationCard(final UserRepresentation userRepresentation) {
		this.userRepresentation = userRepresentation;
		this.registrations = new HashSet<HandlerRegistration>();
		isCurrentUserSuperUser = false;

		userName = new EditableLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				if (text.isEmpty() || text.equals(user.getName())) return false;
				if (!user.equals(ClientServices.getCurrentUser())) return false;
				user.setName(text);

				SERVICE_PROVIDER.userData().onUserDataUpdate(user, new AsyncCallback<User>() {

					@Override
					public void onSuccess(final User result) {
						SERVICE_PROVIDER.alerting().showSuccess(messages.userNameChangeSuccess());
					}

					@Override
					public void onFailure(final Throwable caught) {
						SERVICE_PROVIDER.alerting().showError(messages.userNameChangeFailure());
					}
				});
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		});

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void show() {
		addListeners();
		updateCheckBoxes();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		removeListeners();
		CloseEvent.fire(this, this);
	}

	@UiHandler("readOnlyCheckBox")
	void onCanMakeChangesValueChange(final ValueChangeEvent<Boolean> e) {
		ClientServices.get().actionExecution().onUserActionExecutionRequest(new TeamDeclareReadOnlyAction(userRepresentation.getId(), e.getValue()));
	}

	@UiHandler("canInviteCheckBox")
	void onCanInviteValueChange(final ValueChangeEvent<Boolean> e) {
		if (e.getValue() && userRepresentation.isReadOnly()) {
			ClientServices.get().actionExecution().onUserActionExecutionRequest(new TeamDeclareReadOnlyAction(userRepresentation.getId(), false));
		}
		ClientServices.get().actionExecution().onUserActionExecutionRequest(new TeamDeclareCanInviteAction(userRepresentation.getId(), e.getValue()));
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<UserInformationCard> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	public void updateUser(final User user) {
		this.user = user;
		userName.setReadOnly(isOtherUser());
		updateCheckBoxes();
		updateInformationValues();
	}

	private void updateCheckBoxes() {
		updateCheckBoxesUpdeateability();
		updateCheckBoxesValues();
	}

	private void updateCheckBoxesUpdeateability() {
		try {
			final UserRepresentation currentUser = ClientServices.getCurrentProjectContext().findUser(ClientServices.getCurrentUser());
			final boolean isOtherUser = isOtherUser();
			final boolean canMakeChanges = isOtherUser && isCurrentUserSuperUser && !currentUser.isReadOnly();
			readOnlyCheckBox.setEnabled(canMakeChanges);
			canInviteCheckBox.setEnabled(canMakeChanges && currentUser.canInvite());
		} catch (final UserNotFoundException e) {
			readOnlyCheckBox.setEnabled(false);
			canInviteCheckBox.setEnabled(false);
		}
	}

	private boolean isOtherUser() {
		return !user.equals(ClientServices.getCurrentUser());
	}

	private void updateCheckBoxesValues() {
		readOnlyCheckBox.setValue(userRepresentation.isReadOnly(), false);
		canInviteCheckBox.setValue(!userRepresentation.isReadOnly() && userRepresentation.canInvite(), false);
	}

	private void showLabel() {
		userImageContainer.setStyleName(style.userImageContainerImageColor(), false);
		userImageContainer.setStyleName(style.userImageContainerLabelColor(), true);
		userImageContainer.showWidget(1);
	}

	private void showImage() {
		userImageContainer.setStyleName(style.userImageContainerLabelColor(), false);
		userImageContainer.setStyleName(style.userImageContainerImageColor(), true);
		userImageContainer.showWidget(0);
	}

	private void updateInformationValues() {
		userEmail.setText(user.getEmail());
		userName.setValue(user.getName());
		userWithoutImage.setText(user.getName().substring(0, 1));
		author.setUrl(SERVICE_PROVIDER.userData().getAvatarUrl(user));

		SERVICE_PROVIDER.userData().hasAvatarInGravatar(user, new UserHasGravatarCallback() {
			@Override
			public void onResponseReceived(final boolean hasGravatarAvatar) {
				if (hasGravatarAvatar) showImage();
				else showLabel();
			}
		});

		userImageContainer.showWidget(1);
	}

	private void addListeners() {
		if (!registrations.isEmpty()) return;

		registrations.add(ClientServices.get().actionExecution().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
					final boolean isUserAction) {
				if (action instanceof TeamAction && action.getReferenceId().equals(userRepresentation.getId())) updateCheckBoxesValues();
			}

		}));

		registrations.add(ClientServices.get().userData().registerListenerForSpecificUser(ClientServices.getCurrentUser(), new UserSpecificInformationChangeListener() {
			@Override
			public void onInformationChange(final User user) {
				isCurrentUserSuperUser = user.isSuperUser();
				updateCheckBoxesUpdeateability();
			}
		}));
	}

	private void removeListeners() {
		final HashSet<HandlerRegistration> currentRegistrations = new HashSet<HandlerRegistration>(registrations);
		for (final HandlerRegistration reg : currentRegistrations) {
			reg.removeHandler();
		}

		registrations.removeAll(currentRegistrations);
	}

}
