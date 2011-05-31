package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelItemWidget extends Composite {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleasePanelItemWidget> {}

	@UiField
	protected Label header;

	@UiField
	protected VerticalPanel releaseContainer;

	@UiField
	protected VerticalPanel scopeContainer;

	public ReleasePanelItemWidget(final Release release) {
		initWidget(uiBinder.createAndBindUi(this));

		header.setText(release.getDescription());
		for (final Release childRelease : release.getChildReleases()) {
			releaseContainer.add(new ReleasePanelItemWidget(childRelease));
		}
		for (final Scope scope : release.getScopeList()) {
			scopeContainer.add(new Label(scope.getDescription()));
		}
		reviewContainersVisibility();
	}

	private void reviewContainersVisibility() {
		if (releaseContainer.getWidgetCount() == 0) releaseContainer.setVisible(false);
		if (scopeContainer.getWidgetCount() == 0) scopeContainer.setVisible(false);
	}
}
