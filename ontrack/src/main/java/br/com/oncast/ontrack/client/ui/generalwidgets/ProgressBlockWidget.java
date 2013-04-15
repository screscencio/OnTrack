package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBlockWidget extends Composite implements HasText {

	private static InformationBlockWidgetUiBinder uiBinder = GWT.create(InformationBlockWidgetUiBinder.class);

	interface InformationBlockWidgetUiBinder extends UiBinder<Widget, ProgressBlockWidget> {}

	@UiField
	FocusPanel rootPanel;

	@UiField
	SpanElement accomplished;

	@UiField
	SpanElement accomplishedDecimal;

	@UiField
	SpanElement valueSeparator;

	@UiField
	SpanElement total;

	@UiField
	SpanElement totalDecimal;

	@UiField
	DivElement progressBar;

	@UiField
	SpanElement posfix;

	@UiField
	FastLabel description;

	public ProgressBlockWidget() {
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

	public void setValue(final Float accomplished, final Float total) {
		if (total == null || total < 0.1) {
			setAsNull(total == null);
			return;
		}

		setAccomplishedVisible(true);
		setValue(this.accomplished, this.accomplishedDecimal, accomplished);
		setValue(this.total, this.totalDecimal, total);
		setProgressBarPercent(accomplished / total);
	}

	private void setProgressBarPercent(final float percent) {
		progressBar.getStyle().setWidth(percent * 100, Unit.PCT);
	}

	public void setTotal(final Float number) {}

	private void setValue(final Element valueElement, final Element decimalElement, final Float number) {
		final String[] rounded = ClientDecimalFormat.roundFloat(number == null ? 0 : number, 1).split("\\.");
		valueElement.setInnerText(rounded[0]);
		if (rounded.length > 1 && !"0".equals(rounded[0])) decimalElement.setInnerText("." + rounded[1]);
	}

	public void setPosfix(final String text) {
		posfix.setInnerText(text);
	}

	private void setAsNull(final boolean isNull) {
		setAccomplishedVisible(false);

		if (isNull) total.setInnerText("---");
		else setValue(this.total, this.totalDecimal, 0F);
		setProgressBarPercent(0);
	}

	private void setAccomplishedVisible(final boolean visible) {
		ElementUtils.setVisible(accomplished, visible);
		ElementUtils.setVisible(accomplishedDecimal, visible);
		ElementUtils.setVisible(valueSeparator, visible);
	}
}
