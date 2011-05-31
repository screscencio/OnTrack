package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelWidget extends Composite {

	private static ReleasePanelWidgetUiBinder uiBinder = GWT.create(ReleasePanelWidgetUiBinder.class);

	interface ReleasePanelWidgetUiBinder extends UiBinder<Widget, ReleasePanelWidget> {}

	@UiField
	protected VerticalPanel releasePanel;

	public ReleasePanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setReleases(final List<Release> releases) {
		releasePanel.clear();

		for (final Release release : releases) {
			releasePanel.add(new ReleasePanelItemWidget(release));
		}
	}
}
