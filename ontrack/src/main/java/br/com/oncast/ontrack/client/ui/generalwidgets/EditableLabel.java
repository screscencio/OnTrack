package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;

public class EditableLabel extends Composite implements HasValueChangeHandlers<String>, HasValue<String>, HasDoubleClickHandlers {

	private static final int DOUBLE_CLICK_DELAY = 250;

	private static EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

	interface EditableLabelUiBinder extends UiBinder<Widget, EditableLabel> {}

	interface EditableLabelStyle extends CssResource {
		String disabled();
	}

	@UiField
	EditableLabelStyle style;

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel deckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected Label visualizationLabel;

	@UiField
	@IgnoredByDeepEquality
	protected TextBox editionBox;

	@IgnoredByDeepEquality
	private final EditableLabelEditionHandler editionHandler;

	private boolean isReadOnly = false;

	private Timer timer;

	public EditableLabel(final EditableLabelEditionHandler editionHandler) {
		this(editionHandler, false);
	}

	public EditableLabel(final EditableLabelEditionHandler editionHandler, final boolean wrapText) {
		this.editionHandler = editionHandler;
		initWidget(uiBinder.createAndBindUi(this));
		deckPanel.showWidget(0);
		if (wrapText) visualizationLabel.removeStyleName("v-ellip");
	}

	@Override
	public String getValue() {
		return visualizationLabel.getText();
	}

	public void setReadOnly(final boolean readOnly) {
		isReadOnly = readOnly;

		visualizationLabel.setStyleName(style.disabled(), readOnly);
	}

	@Override
	public void setValue(final String value) {
		editionBox.setValue(value);
		visualizationLabel.setText(value);
	}

	@Override
	public void setValue(final String value, final boolean fireEvents) {
		if (fireEvents) ValueChangeEvent.fireIfNotEqual(this, getValue(), value);
		setValue(value);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@UiHandler("visualizationLabel")
	protected void onClick(final ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		if (timer == null) switchToEdit();
		else timer.schedule(DOUBLE_CLICK_DELAY);
	}

	@UiHandler("visualizationLabel")
	protected void onDoubleClick(final DoubleClickEvent event) {
		if (timer != null) timer.cancel();
	}

	@UiHandler("editionBox")
	protected void onBlur(final BlurEvent event) {
		switchToVisualization(true);
	}

	@UiHandler("editionBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!isEditionMode()) return;

		event.stopPropagation();

		final boolean isEnterOrTab = event.getNativeKeyCode() == KEY_ENTER || event.getNativeKeyCode() == KEY_TAB;
		if (isEnterOrTab || event.getNativeKeyCode() == KEY_ESCAPE) {
			event.preventDefault();
			switchToVisualization(isEnterOrTab);
		} else return;
	}

	public void switchToEdit() {
		if (isEditionMode() || isReadOnly) return;

		editionBox.setText(getValue());
		deckPanel.showWidget(1);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				editionBox.selectAll();
				editionBox.setFocus(true);
				editionHandler.onEditionStart();
			}
		});
	}

	public void switchToVisualization(final boolean shouldTryToUpdateChanges) {
		if (!isEditionMode()) return;
		deckPanel.showWidget(0);
		editionHandler.onEditionExit(!shouldTryToUpdateChanges);

		if (!shouldTryToUpdateChanges) {
			editionBox.setText(visualizationLabel.getText());
		} else if (!getValue().equals(editionBox.getText()) || editionBox.getText().isEmpty()) {
			if (editionHandler.onEditionRequest(editionBox.getText())) setValue(editionBox.getText(), true);
			else editionBox.setText(getValue());
		}
	}

	public boolean isEditionMode() {
		return deckPanel.getVisibleWidget() == 1;
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(final DoubleClickHandler handler) {
		if (timer == null) timer = new Timer() {
			@Override
			public void run() {
				switchToEdit();
			}
		};
		return visualizationLabel.addDoubleClickHandler(handler);
	}
}
