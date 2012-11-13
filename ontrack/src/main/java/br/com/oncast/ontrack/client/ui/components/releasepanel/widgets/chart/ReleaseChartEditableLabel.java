package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseChartEditableLabel extends Composite implements HasValue<Float>, HasText, HasMouseOverHandlers, HasMouseOutHandlers {

	private static ReleaseChartEditableLabelUiBinder uiBinder = GWT.create(ReleaseChartEditableLabelUiBinder.class);

	interface ReleaseChartEditableLabelUiBinder extends UiBinder<Widget, ReleaseChartEditableLabel> {}

	@UiField
	TextBox valueEdit;

	@UiField
	Label label;

	@UiField
	Label valueLabel;

	@UiField
	Label remove;

	@UiField
	FocusPanel focusPanel;

	private boolean isRemoveAvailable;

	private NativeEventListener globalNativeMouseUpListener;

	public ReleaseChartEditableLabel() {
		initWidget(uiBinder.createAndBindUi(this));
		hideEdit();
	}

	@UiHandler("valueLabel")
	protected void onValueClick(final ClickEvent e) {
		valueEdit.setValue(valueLabel.getText(), false);
		valueEdit.setVisible(true);
		valueEdit.setFocus(true);
	}

	@UiHandler("remove")
	protected void onRemoveClick(final ClickEvent e) {
		if (remove.getElement().getStyle().getVisibility().equals(Visibility.HIDDEN.getCssName())) return;

		setValue(null);
		hideEdit();
	}

	@UiHandler("focusPanel")
	protected void onMouseOver(final MouseOverEvent e) {
		hideRemoveLabel(false);
		unregisterGlobalNativeMouseUpListener();
	}

	@UiHandler("focusPanel")
	protected void onMouseOut(final MouseOutEvent e) {
		hideRemoveLabel(true);
		GlobalNativeEventService.getInstance().addMouseUpListener(getMouseUpListener());
	}

	private NativeEventListener getMouseUpListener() {
		if (globalNativeMouseUpListener == null) globalNativeMouseUpListener = new NativeEventListener() {
			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				hideEdit();
				unregisterGlobalNativeMouseUpListener();
			}
		};
		return globalNativeMouseUpListener;
	}

	private void unregisterGlobalNativeMouseUpListener() {
		if (globalNativeMouseUpListener == null) return;

		GlobalNativeEventService.getInstance().removeMouseUpListener(getMouseUpListener());
	}

	@UiHandler("focusPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() != KEY_ESCAPE || !valueEdit.isVisible()) return;

		e.stopPropagation();
		hideEdit();
	}

	@UiHandler("valueEdit")
	protected void onBlur(final BlurEvent e) {
		hideEdit();
	}

	@UiHandler("valueEdit")
	protected void onEditKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() != BrowserKeyCodes.KEY_ENTER) return;

		e.stopPropagation();
		try {
			setValue(Float.valueOf(valueEdit.getText()));
			hideEdit();
		}
		catch (final NumberFormatException exception) {}
	}

	public void hideEdit() {
		valueEdit.setVisible(false);
		hideRemoveLabel(true);
	}

	private void hideRemoveLabel(final boolean mustHide) {
		remove.getElement().getStyle().setVisibility(!isRemoveAvailable || mustHide ? Visibility.HIDDEN : Visibility.VISIBLE);
	}

	@Override
	public void setValue(final Float value) {
		setValue(value, true);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Float> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Float getValue() {
		return Float.valueOf(valueLabel.getText());
	}

	@Override
	public void setValue(final Float value, final boolean fireEvents) {
		if (value != null) this.valueLabel.setText(ClientDecimalFormat.roundFloat(value, 1));

		if (fireEvents) ValueChangeEvent.fire(this, value);
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public void setText(final String text) {
		label.setText(text);
	}

	public void setRemoveValueAvailable(final boolean isAvailable) {
		isRemoveAvailable = isAvailable;
	}

	@Override
	public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler) {
		return focusPanel.addMouseOutHandler(handler);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler) {
		return focusPanel.addMouseOverHandler(handler);
	}
}
