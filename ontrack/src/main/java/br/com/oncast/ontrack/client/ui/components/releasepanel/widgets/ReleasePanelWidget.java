package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
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

	private final List<ReleasePanelItemWidget> childWidgetsList;

	private final ReleaseWidgetFactory releaseWidgetFactory = new ReleaseWidgetFactoryImpl();

	public ReleasePanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		childWidgetsList = new ArrayList<ReleasePanelItemWidget>();
	}

	public void init(final List<Release> releases) {
		releasePanel.clear();

		for (final Release release : releases) {
			createNewChild(release);
		}
	}

	public void updateReleases(final List<Release> releases) {
		for (final Release release : releases) {
			final ReleasePanelItemWidget releaseWidget = getReleaseWithDescription(release.getDescription());
			if (releaseWidget == null) createNewChild(release);
			else {
				releaseWidget.updateChildReleases(release.getChildReleases());
				releaseWidget.updateChildScopes(release.getScopeList());
			}
		}
	}

	private ReleasePanelItemWidget createNewChild(final Release release) {
		final ReleasePanelItemWidget childItem = releaseWidgetFactory.createReleaseWidget(release);
		releasePanel.add(childItem);
		childWidgetsList.add(childItem);
		return childItem;
	}

	private ReleasePanelItemWidget getReleaseWithDescription(final String description) {
		for (final ReleasePanelItemWidget childItem : childWidgetsList) {
			if (childItem.getHeader().equals(description)) return childItem;
		}
		return null;
	}

	protected List<ReleasePanelItemWidget> getChildWidgetsList() {
		return childWidgetsList;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ReleasePanelWidget)) return false;
		final ReleasePanelWidget other = (ReleasePanelWidget) obj;

		return childWidgetsList.equals(other.getChildWidgetsList());
	}
}
