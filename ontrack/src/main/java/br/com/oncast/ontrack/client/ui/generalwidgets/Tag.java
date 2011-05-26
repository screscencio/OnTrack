package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Tag extends Composite implements HasText {

	private static ReleaseTagUiBinder uiBinder = GWT.create(ReleaseTagUiBinder.class);

	interface ReleaseTagUiBinder extends UiBinder<Widget, Tag> {}

	@UiField
	protected Label tagLabel;

	@UiField
	protected Label closeLabel;

	public Tag() {
		initWidget(uiBinder.createAndBindUi(this));
		initCloseLabel();
	}

	public Tag(final String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		initCloseLabel();
	}

	@Override
	public String getText() {
		return tagLabel.getText();
	}

	@Override
	public void setText(final String text) {
		tagLabel.setText(text);
	}

	private void initCloseLabel() {
		closeLabel.setText("X");
		closeLabel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				// TODO remove tag.
			}
		});
	}
}
