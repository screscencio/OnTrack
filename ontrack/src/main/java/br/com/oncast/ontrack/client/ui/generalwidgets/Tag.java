package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class Tag extends Composite implements HasText {

	private static ReleaseTagUiBinder uiBinder = GWT.create(ReleaseTagUiBinder.class);

	interface ReleaseTagUiBinder extends UiBinder<Widget, Tag> {}

	@UiField
	protected FastLabel tagLabel;

	@UiField
	protected FocusPanel closeLabel;

	private ClickHandler closeClickHandler;

	private ClickHandler clickHandler;

	private Boolean visible;

	public Tag() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public boolean isVisible() {
		if (visible != null) return visible;
		return visible = super.isVisible();
	}

	@Override
	public void setVisible(final boolean visible) {
		if (isVisible() == visible) return;
		this.visible = visible;
		super.setVisible(visible);
	}

	@Override
	public String getText() {
		return tagLabel.getText();
	}

	/**
	 * It is safe to use the {@link #setText(String)} repeatedly. {@link Tag} makes use of the {@link FastLabel} component.
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(final String text) {
		tagLabel.setText(text);
		tagLabel.setTitle(text);
	}

	/**
	 * It is safe to use the {@link #setTitle(String)} repeatedly. {@link Tag} makes use of the {@link FastLabel} component.
	 * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(final String title) {
		super.setTitle(title);
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
