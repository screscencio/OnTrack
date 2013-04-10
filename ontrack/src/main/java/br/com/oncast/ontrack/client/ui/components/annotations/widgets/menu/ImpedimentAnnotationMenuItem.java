package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImpedimentAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static ImpedimentWidgetUiBinder uiBinder = GWT.create(ImpedimentWidgetUiBinder.class);

	interface ImpedimentWidgetUiBinder extends UiBinder<Widget, ImpedimentAnnotationMenuItem> {}

	@UiField
	FocusPanel icon;

	private final UUID subjectId;

	private final Annotation annotation;

	private boolean readOnly = false;

	public ImpedimentAnnotationMenuItem(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("icon")
	void onClick(final ClickEvent e) {
		if (readOnly || annotation.isDeprecated()) return;
		ClientServices.get().details().markAsImpediment(subjectId, annotation.getId());
	}

	@Override
	public void update() {}

	@Override
	public void setReadOnly(final boolean b) {
		this.readOnly = b;
	}

}
