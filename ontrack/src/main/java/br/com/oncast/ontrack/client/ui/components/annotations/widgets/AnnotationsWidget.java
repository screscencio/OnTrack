package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.Separator;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationsWidget extends Composite {

	private static AnnotationsWidgetUiBinder uiBinder = GWT.create(AnnotationsWidgetUiBinder.class);

	interface AnnotationsWidgetUiBinder extends UiBinder<Widget, AnnotationsWidget> {}

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected ExtendableTextArea newAnnotationText;

	@UiField
	protected VerticalPanel annotations;

	private UUID subjectId;

	public AnnotationsWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("newAnnotationText")
	protected void onNewAnnotationTextKeyDown(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ENTER != e.getNativeKeyCode() || !e.isControlKeyDown()) return;
		e.preventDefault();

		addAnnotation();
		newAnnotationText.setText("");
	}

	@UiHandler("focusPanel")
	protected void disableGlobalShortcuts(final KeyDownEvent e) {
		if (BrowserKeyCodes.KEY_ESCAPE == e.getNativeKeyCode()) return;

		e.stopPropagation();
	}

	public void setFocus(final boolean b) {
		newAnnotationText.setFocus(true);
	}

	private void addAnnotation() {
		final String message = newAnnotationText.getText().trim();
		if (message.isEmpty()) return;

		// FIXME Mats use a service here
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final User user = provider.getAuthenticationService().getCurrentUser();
		provider.getActionExecutionService().onUserActionExecutionRequest(new AnnotationCreateAction(subjectId, user));

		annotations.insert(new AnnotationTopic("admin@ontrack.com", message), 0);
		annotations.insert(new Separator(), 1);
	}

	public void setSubjectId(final UUID id) {
		this.subjectId = id;
	}
}
