package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class InformationBlockWidget extends Composite implements HasText {

	private static InformationBlockWidgetUiBinder uiBinder = GWT.create(InformationBlockWidgetUiBinder.class);

	interface InformationBlockWidgetUiBinder extends UiBinder<Widget, InformationBlockWidget> {}

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

}
