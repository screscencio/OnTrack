package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import java.util.Date;

import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SinceAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static SinceAnnotationMenuItemUiBinder uiBinder = GWT.create(SinceAnnotationMenuItemUiBinder.class);

	interface SinceAnnotationMenuItemUiBinder extends UiBinder<Widget, SinceAnnotationMenuItem> {}

	@UiField
	FocusPanel icon;

	@UiField
	Label label;

	private final Annotation annotation;

	public SinceAnnotationMenuItem(final Annotation annotation) {
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));

		update();
	}

	@Override
	public void update() {
		final Date date = annotation.getTimestampFor(annotation.getType());
		label.setText(HumanDateFormatter.getRelativeDate(date) + " (" + HumanDateFormatter.getDifferenceDate(date) + ")");
		label.setTitle(HumanDateFormatter.getAbsoluteText(date));
	}

	@Override
	public void setReadOnly(final boolean b) {}

}
