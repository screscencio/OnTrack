package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectSelectionWidget;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMenuWidget extends Composite implements HasCloseHandlers<ProjectMenuWidget>, PopupAware {

	private static ProjectMenuWidgetUiBinder uiBinder = GWT.create(ProjectMenuWidgetUiBinder.class);

	interface ProjectMenuWidgetUiBinder extends UiBinder<Widget, ProjectMenuWidget> {}

	@UiField
	Anchor exportMapLink;

	@UiField
	ProjectSelectionWidget projectSelectionWidget;

	@UiFactory
	ProjectSelectionWidget createProjectSelectionWidget() {
		return ProjectSelectionWidget.forProjectSwitchingMenu();
	}

	public ProjectMenuWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		projectSelectionWidget.addCloseHandler(new CloseHandler<ProjectSelectionWidget>() {
			@Override
			public void onClose(final CloseEvent<ProjectSelectionWidget> event) {
				CloseEvent.fire(ProjectMenuWidget.this, ProjectMenuWidget.this);
			}
		});
	}

	@Override
	public void show() {
		projectSelectionWidget.show();
		projectSelectionWidget.focus();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		projectSelectionWidget.hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ProjectMenuWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@UiHandler("exportMapLink")
	protected void onExportMapLinkClick(final ClickEvent event) {
		exportData();
	}

	private void exportData() {
		final String url = URLBuilder.buildMindMapExportURL(ClientServiceProvider.getInstance().getProjectRepresentationProvider().getCurrent().getId());
		exportMapLink.setHref(url);
		hide();
	}
}
