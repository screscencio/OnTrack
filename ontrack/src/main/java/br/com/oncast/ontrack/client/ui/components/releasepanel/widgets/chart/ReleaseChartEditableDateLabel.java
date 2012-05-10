package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.Date;

import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class ReleaseChartEditableDateLabel extends Composite implements HasText, HasValue<Date> {

	private static ReleaseChartEditableDateLabelUiBinder uiBinder = GWT.create(ReleaseChartEditableDateLabelUiBinder.class);

	interface ReleaseChartEditableDateLabelUiBinder extends UiBinder<Widget, ReleaseChartEditableDateLabel> {}

	@UiField
	DatePicker datePicker;

	@UiField
	FocusPanel focusPanel;

	@UiField
	Label label;

	@UiField
	Label value;

	private Date date;

	public ReleaseChartEditableDateLabel() {
		initWidget(uiBinder.createAndBindUi(this));
		hidePicker();
	}

	@UiHandler("value")
	protected void onClick(final ClickEvent e) {
		datePicker.setVisible(true);
		focusPanel.setFocus(true);
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
		return date;
	}

	@Override
	public void setValue(final Date date, final boolean fireEvents) {
		this.date = date;
		this.value.setText(WorkingDayFactory.create(date).getDayMonthShortYearString());

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
}
