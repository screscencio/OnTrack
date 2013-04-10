package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
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

public class SolveImpedimentAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static SolveImpedimentAnnotationMenuItemUiBinder uiBinder = GWT.create(SolveImpedimentAnnotationMenuItemUiBinder.class);

	interface SolveImpedimentAnnotationMenuItemUiBinder extends UiBinder<Widget, SolveImpedimentAnnotationMenuItem> {}

	@UiField
	FocusPanel icon;

	private final UUID subjectId;

	private final Annotation annotation;

	private boolean readOnly = false;

	public SolveImpedimentAnnotationMenuItem(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("icon")
	void onClick(final ClickEvent e) {
		if (readOnly || annotation.isDeprecated()) return;
		ClientServiceProvider.getInstance().getDetailsService().markAsSolveImpediment(subjectId, annotation.getId());
	}

	@Override
	public void update() {}

	@Override
	public void setReadOnly(final boolean b) {
		this.readOnly = b;
	}

}
