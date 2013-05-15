package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Date;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DatePickerPopup extends Composite implements PopupAware, HasValueChangeHandlers<Date>, HasCloseHandlers<DatePickerPopup> {

	private static DatePickerPopupUiBinder uiBinder = GWT.create(DatePickerPopupUiBinder.class);

	interface DatePickerPopupUiBinder extends UiBinder<Widget, DatePickerPopup> {}

	public DatePickerPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	DatePicker picker;

	@UiField
	Button removeBtn;

	public DatePickerPopup(final Date value) {
		initWidget(uiBinder.createAndBindUi(this));
		picker.setValue(value, false);
	}

	@UiHandler("removeBtn")
	void onClick(final ClickEvent e) {
		picker.setValue(null, true);
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
		return picker.addValueChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<DatePickerPopup> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

}
