package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMenuWidget extends Composite implements HasCloseHandlers<ProjectMenuWidget> {

	private static ProjectMenuWidgetUiBinder uiBinder = GWT.create(ProjectMenuWidgetUiBinder.class);

	interface ProjectMenuWidgetUiBinder extends UiBinder<Widget, ProjectMenuWidget> {}

	public ProjectMenuWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ProjectMenuWidget> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}
}
