package br.com.oncast.ontrack.client.ui.component.scopetree.widgets;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTreeItemWidget extends Composite {

	private static EditableLabelUiBinder uiBinder = GWT.create(EditableLabelUiBinder.class);

	interface EditableLabelUiBinder extends UiBinder<Widget, ScopeTreeItemWidget> {}

	@UiField
	protected DeckPanel deckPanel;

	@UiField
	protected Label descriptionLabel;

	@UiField
	protected TextBox editionBox;

	@UiField
	protected Label releaseTag;

	@UiField
	protected FocusPanel focusPanel;

	private final ScopeTreeItemWidgetEditionHandler editionHandler;

	public ScopeTreeItemWidget(final String text, final ScopeTreeItemWidgetEditionHandler editionHandler) {

		initWidget(uiBinder.createAndBindUi(this));

		descriptionLabel.setText(text);
		this.editionHandler = editionHandler;

		releaseTag.setText("Release 1 / Iteration 1");

		focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(final DoubleClickEvent event) {
				switchToEditionMode();
			}
		});

		editionBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				switchToVisualization();
			}
		});

		editionBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (!isEditing()) return;
				event.preventDefault();
				event.stopPropagation();

				if (event.getNativeKeyCode() == KEY_ENTER) {
					switchToVisualization();
				}
				else if (event.getNativeKeyCode() == KEY_ESCAPE) {
					editionBox.setText(descriptionLabel.getText());
					switchToVisualization();
				}
			}
		});

		deckPanel.showWidget(0);
	}

	public String getValue() {
		return descriptionLabel.getText().trim() + " @" + releaseTag.getText().trim();
	}

	public void setValue(final String value) {
		// final MatchResult exec = RegExp.compile("[a-zA-Z0-9][^@?][a-zA-Z0-9]*").exec(value);
		// TODO make regex.
		descriptionLabel.setText(value);
		editionBox.setText(value);
	}

	public void switchToEditionMode() {
		if (isEditing()) return;

		editionBox.setText(getValue());
		deckPanel.showWidget(1);
		new Timer() {

			@Override
			public void run() {
				editionBox.selectAll();
				editionBox.setFocus(true);
			}
		}.schedule(200);
	}

	public void switchToVisualization() {
		if (!isEditing()) return;
		deckPanel.showWidget(0);
		if (!descriptionLabel.getText().equals(editionBox.getText())) editionHandler.onEdit(editionBox.getText());
	}

	private boolean isEditing() {
		return deckPanel.getVisibleWidget() == 1;
	}
}
