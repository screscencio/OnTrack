package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class InformationBlockWidget extends Composite implements HasText, HasClickHandlers {

	private static InformationBlockWidgetUiBinder uiBinder = GWT.create(InformationBlockWidgetUiBinder.class);

	interface InformationBlockWidgetUiBinder extends UiBinder<Widget, InformationBlockWidget> {}

	@UiField
	FocusPanel rootPanel;

	@UiField
	SpanElement value;

	@UiField
	SpanElement decimal;

	@UiField
	SpanElement posfix;

	@UiField
	FastLabel description;

	public InformationBlockWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText() {
		return description.getText();
	}

	@Override
	public void setText(final String text) {
		this.description.setText(text);

	}

	public void setColor(final String colorCode) {
		description.getElement().getStyle().setColor(colorCode);
	}

	public void setValue(final Float number) {
		setValue(number == null ? null : ClientDecimalFormat.roundFloat(number, 1));
	}

	public void setValue(final Date date) {
		if (date == null) {
			setAsNull();
			return;
		}

		this.value.setInnerText(DateTimeFormat.getFormat("dd").format(date));
		this.decimal.setInnerText(DateTimeFormat.getFormat("/MM").format(date));
		this.posfix.setInnerText(DateTimeFormat.getFormat("yyyy").format(date));
	}

	public void setValue(final WorkingDay day) {
		if (day == null) {
			setAsNull();
			return;
		}

		final String[] dayStr = day.getDayAndMonthString().split("/");
		this.value.setInnerText(dayStr[0]);
		if (dayStr.length > 1) this.decimal.setInnerText("/" + dayStr[1]);
		this.posfix.setInnerText("" + day.getYear());
	}

	public void setValue(final String numberStr) {
		if (numberStr == null || numberStr.isEmpty()) {
			setAsNull();
			return;
		}

		final String[] rounded = numberStr.split("\\.");
		this.value.setInnerText(rounded[0]);
		if (rounded.length > 1 && !"0".equals(rounded[0])) this.decimal.setInnerText("." + rounded[1]);
	}

	public void setPosfix(final String text) {
		this.posfix.setInnerText(text);
	}

	public void setAsNull() {
		this.value.setInnerText("---");
		this.decimal.setInnerText("");
		this.posfix.setInnerText("");
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return rootPanel.addClickHandler(handler);
	}

}
