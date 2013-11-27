package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SinceAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static SinceAnnotationMenuItemMessages messages = GWT.create(SinceAnnotationMenuItemMessages.class);

	private static SinceAnnotationMenuItemUiBinder uiBinder = GWT.create(SinceAnnotationMenuItemUiBinder.class);

	interface SinceAnnotationMenuItemUiBinder extends UiBinder<Widget, SinceAnnotationMenuItem> {}

	@UiField
	FocusPanel icon;

	@UiField
	Label label;

	private final Annotation annotation;

	private final CustomDuration duration;

	public SinceAnnotationMenuItem(final Annotation annotation, final CustomDuration duration) {
		this.annotation = annotation;
		this.duration = duration;

		initWidget(uiBinder.createAndBindUi(this));

	}

	public SinceAnnotationMenuItem(final Annotation annotation) {
		this(annotation, null);
	}

	@Override
	protected void onLoad() {
		update();
	}

	@Override
	public void update() {
		final Date date = annotation.getLastOcuurenceOf(annotation.getType());
		final String customDuration = duration == null ? "" : " - " + messages.duration() + duration.getDurationText(annotation);

		label.setText(HumanDateFormatter.get().formatDateRelativeToNow(date) + customDuration);
		label.setTitle(HumanDateFormatter.formatAbsoluteDate(date));
	}

	@Override
	public void setReadOnly(final boolean b) {}

}
