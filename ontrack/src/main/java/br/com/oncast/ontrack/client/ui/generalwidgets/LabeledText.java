package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LabeledText extends Composite implements HasText {

	private static LabeledTextUiBinder uiBinder = GWT.create(LabeledTextUiBinder.class);

	interface LabeledTextUiBinder extends UiBinder<Widget, LabeledText> {}

	public LabeledText() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label label;

	@UiField
	Label value;

	public void setLabel(final String text) {
		label.setText(text);
	}

	@Override
	public void setText(final String text) {
		value.setText(text);
	}

	@Override
	public String getText() {
		return value.getText();
	}

}
