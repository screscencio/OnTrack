package br.com.oncast.ontrack.client.ui.components.organization.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.feedback.SendFeedbackCallback;
import br.com.oncast.ontrack.client.ui.components.footerbar.FeedbackPopupMessages;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ProjectSummaryWidget extends Composite implements ModelWidget<ProjectRepresentation> {

	private static final String NEW_LINE = "\n";

	private static FeedbackPopupMessages messages = GWT.create(FeedbackPopupMessages.class);

	private static ProjectSummaryWidgetUiBinder uiBinder = GWT.create(ProjectSummaryWidgetUiBinder.class);

	interface ProjectSummaryWidgetUiBinder extends UiBinder<Widget, ProjectSummaryWidget> {}

	@UiField
	Label name;

	@UiField
	UIObject bodyContent;

	@UiField
	TextArea feedbackArea;

	@UiField
	Button sendFeedbackButton;

	private final ProjectRepresentation project;

	public ProjectSummaryWidget(final ProjectRepresentation project) {
		this.project = project;
		initWidget(uiBinder.createAndBindUi(this));

		name.setText(project.getName());

		setContainerState(false);
	}

	@UiHandler("sendFeedbackButton")
	void onSendFeedbackButtonClicked(final ClickEvent e) {
		final String feedbackMessage = feedbackArea.getText();
		if (feedbackMessage.trim().isEmpty()) {
			feedbackArea.setFocus(true);
			return;
		}

		ClientServiceProvider.getInstance().getFeedbackService().sendFeedback(feedbackMessage.replaceAll(NEW_LINE, "<br/>"), new SendFeedbackCallback() {
			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				feedbackArea.setEnabled(true);
				sendFeedbackButton.setEnabled(true);
				ClientServiceProvider.getInstance().getClientAlertingService().showError(messages.feedbackNotSent());
			}

			@Override
			public void onFeedbackSentSucessfully() {
				feedbackArea.setEnabled(true);
				sendFeedbackButton.setEnabled(true);
				feedbackArea.setText("");
				ClientServiceProvider.getInstance().getClientAlertingService().showSuccess(messages.feedbackSent());
			}
		});
		feedbackArea.setEnabled(false);
		sendFeedbackButton.setEnabled(false);
		ClientServiceProvider.getInstance().getClientAlertingService().showInfo(messages.processingYourFeedback());
	}

	@UiHandler("name")
	void onNameClicked(final ClickEvent e) {
		setContainerState(!getContainerState());
	}

	@UiHandler("planningLink")
	void onPlanningLinkClicked(final ClickEvent e) {
		ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(new PlanningPlace(project));
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public ProjectRepresentation getModelObject() {
		return project;
	}

	private void setContainerState(final boolean b) {
		bodyContent.setVisible(b);
	}

	private boolean getContainerState() {
		return bodyContent.isVisible();
	}

}
