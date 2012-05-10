package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.Date;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

// FIXME remove
public class DatePickerPopup extends Composite implements HasCloseHandlers<DatePickerPopup>, PopupAware, HasValue<Date> {

	private static DatePickerPopupUiBinder uiBinder = GWT.create(DatePickerPopupUiBinder.class);

	interface DatePickerPopupUiBinder extends UiBinder<Widget, DatePickerPopup> {}

	public DatePickerPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	FocusPanel rootPanel;

	@UiField
	DatePicker picker;

	@UiHandler("rootPanel")
	void onKeyDown(final KeyDownEvent e) {
		if (e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hide();
	}

	@UiHandler("picker")
	void onDatePicked(final ValueChangeEvent<Date> e) {
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<DatePickerPopup> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (isVisible()) CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
		return picker.addValueChangeHandler(handler);
	}

	@Override
	public Date getValue() {
		return picker.getValue();
	}

	@Override
	public void setValue(final Date value) {
		picker.setValue(value);
	}

	@Override
	public void setValue(final Date value, final boolean fireEvents) {
		picker.setValue(value, fireEvents);
	}
}
