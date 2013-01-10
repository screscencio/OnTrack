package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseTag extends Composite implements HasText, HasClickHandlers {

	private static ReleaseTagUiBinder uiBinder = GWT.create(ReleaseTagUiBinder.class);

	interface ReleaseTagUiBinder extends UiBinder<Widget, ReleaseTag> {}

	@UiField
	protected FastLabel tagLabel;

	@UiField
	protected FocusPanel container;

	private Boolean visible;

	public ReleaseTag() {
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
	 * It is safe to use the {@link #setText(String)} repeatedly. {@link ReleaseTag} makes use of the {@link FastLabel} component.
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(final String text) {
		tagLabel.setText(text);
		tagLabel.setTitle(text);
	}

	/**
	 * It is safe to use the {@link #setTitle(String)} repeatedly. {@link ReleaseTag} makes use of the {@link FastLabel} component.
	 * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(final String title) {
		super.setTitle(title);
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return container.addClickHandler(handler);
	}
}
