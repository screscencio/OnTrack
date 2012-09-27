package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class DeprecateAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static DeprecateAnnotationMenuItemUiBinder uiBinder = GWT.create(DeprecateAnnotationMenuItemUiBinder.class);

	private static final DeprecateAnnotationMenuItemMessages messages = GWT.create(DeprecateAnnotationMenuItemMessages.class);

	interface DeprecateAnnotationMenuItemUiBinder extends UiBinder<Widget, DeprecateAnnotationMenuItem> {}

	interface AnnotationDeprecateWidgetStyle extends CssResource {
		String iconActive();
	}

	@UiField
	FocusPanel icon;

	@UiField
	AnnotationDeprecateWidgetStyle style;

	private final UUID subjectId;

	private final Annotation annotation;

	private boolean readOnly = false;

	public DeprecateAnnotationMenuItem(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("icon")
	void onClick(final ClickEvent e) {
		if (readOnly) return;

		if (annotation.isDeprecated()) ClientServiceProvider.getInstance().getAnnotationService().removeDeprecation(subjectId, annotation.getId());
		else ClientServiceProvider.getInstance().getAnnotationService().deprecateAnnotation(subjectId, annotation.getId());
	}

	@Override
	public void update() {
		icon.setStyleName(style.iconActive(), annotation.isDeprecated());
		icon.setTitle(annotation.isDeprecated() ? messages.removeDeprecation() : messages.deprecate());
	}

	@Override
	public void setReadOnly(final boolean b) {
		this.readOnly = b;
	}

}
