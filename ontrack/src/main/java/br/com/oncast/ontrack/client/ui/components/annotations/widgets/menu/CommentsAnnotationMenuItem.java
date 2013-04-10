package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CommentsAnnotationMenuItem extends Composite implements HasClickHandlers, AnnotationMenuItem {

	private static CommentsAnnotationMenuItemUiBinder uiBinder = GWT.create(CommentsAnnotationMenuItemUiBinder.class);

	interface CommentsAnnotationMenuItemUiBinder extends UiBinder<Widget, CommentsAnnotationMenuItem> {}

	public CommentsAnnotationMenuItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	FocusPanel icon;

	@UiField
	Label label;

	private Annotation annotation;

	public CommentsAnnotationMenuItem(final Annotation annotation) {
		this.annotation = annotation;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	public void update() {
		final List<Annotation> comments = ClientServices.getCurrentProjectContext()
				.findAnnotationsFor(annotation.getId());
		label.setText("" + comments.size());
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return icon.addClickHandler(handler);
	}

	@Override
	public void setReadOnly(final boolean b) {}

}
