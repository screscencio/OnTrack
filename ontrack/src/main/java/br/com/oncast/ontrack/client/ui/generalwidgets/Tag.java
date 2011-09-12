package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	private ClickHandler closeClickHandler;

	private ClickHandler clickHandler;

	public Tag() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText() {
		return tagLabel.getText();
	}

	@Override
	public void setText(final String text) {
		tagLabel.setText(text);
	}

	public void setCloseButtonClickHandler(final ClickHandler clickHandler) {
		this.closeClickHandler = clickHandler;
	}

	public void setClickHandler(final ClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}

	@UiHandler("tagLabel")
	protected void addTagClickHandler(final ClickEvent event) {
		if (clickHandler == null) return;
		clickHandler.onClick(event);
	}

	@UiHandler("closeLabel")
	protected void addCloseClickHandler(final ClickEvent event) {
		if (closeClickHandler == null) return;
		closeClickHandler.onClick(event);
	}
}
