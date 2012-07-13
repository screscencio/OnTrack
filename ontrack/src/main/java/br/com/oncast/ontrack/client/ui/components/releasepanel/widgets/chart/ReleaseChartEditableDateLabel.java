package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.Date;

import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class ReleaseChartEditableDateLabel extends Composite implements HasText, HasValue<Date> {

	private static ReleaseChartEditableDateLabelUiBinder uiBinder = GWT.create(ReleaseChartEditableDateLabelUiBinder.class);

	interface ReleaseChartEditableDateLabelUiBinder extends UiBinder<Widget, ReleaseChartEditableDateLabel> {}

	@UiField
	DatePicker datePicker;

	@UiField
	Label label;

	@UiField
	Label value;

	@UiField
	Label remove;

	private boolean isRemoveAvailable;

	private NativeEventListener globalNativeMouseUpListener;

	public ReleaseChartEditableDateLabel() {
		initWidget(uiBinder.createAndBindUi(this));
		hidePicker();
	}

	@UiHandler("value")
	protected void onValueClick(final ClickEvent e) {
		if (datePicker.isVisible()) hidePicker();
		else datePicker.setVisible(true);
	}

	@UiHandler("remove")
	protected void onRemoveClick(final ClickEvent e) {
		if (remove.getElement().getStyle().getVisibility().equals(Visibility.HIDDEN.getCssName())) return;

		setValue(null);
		hidePicker();
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
				hidePicker();
				unregisterGlobalNativeMouseUpListener();
			}
		};
		return globalNativeMouseUpListener;
	}

	private void unregisterGlobalNativeMouseUpListener() {
		GlobalNativeEventService.getInstance().removeMouseUpListener(getMouseUpListener());
	}

	@UiHandler("focusPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() != KEY_ESCAPE || !datePicker.isVisible()) return;

		e.stopPropagation();
		hidePicker();
	}

	@UiHandler("datePicker")
	protected void onValueChange(final ValueChangeEvent<Date> e) {
		setValue(e.getValue());
		hidePicker();
	}

	public void hidePicker() {
		datePicker.setVisible(false);
		hideRemoveLabel(true);
	}

	private void hideRemoveLabel(final boolean mustHide) {
		remove.getElement().getStyle().setVisibility(!isRemoveAvailable || mustHide ? Visibility.HIDDEN : Visibility.VISIBLE);
	}

	@Override
	public void setValue(final Date date) {
		setValue(date, true);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Date getValue() {
		return this.datePicker.getValue();
	}

	@Override
	public void setValue(final Date date, final boolean fireEvents) {
		this.datePicker.setValue(date, false);
		if (date != null) this.value.setText(WorkingDayFactory.create(date).getDayMonthShortYearString());

		if (fireEvents) ValueChangeEvent.fire(this, date);
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
}
