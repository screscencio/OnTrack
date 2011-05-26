package br.com.oncast.ontrack.client.ui.place.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final PlanningView scopeView = new PlanningPanel();
		scopeView.setScope(getScope());
		scopeView.setReleases(getReleases());
		panel.setWidget(scopeView);
	}

	private List<Release> getReleases() {
		final List<Release> releases = new ArrayList<Release>();
		releases.add(new Release("R1"));
		releases.add(new Release("R2"));
		releases.add(new Release("R3"));

		return releases;
	}

	private Scope getScope() {
		final Scope projectScope = new Scope("Project");
		final Scope child = new Scope("aaa");
		child.add(new Scope("111"));
		child.add(new Scope("222"));
		child.add(new Scope("333"));
		child.add(new Scope("444"));
		projectScope.add(child);
		projectScope.add(new Scope("bbb"));
		projectScope.add(new Scope("ccc"));
		projectScope.add(new Scope("ddd"));
		projectScope.add(new Scope("eee"));
		projectScope.add(new Scope("fff"));

		return projectScope;
	}
}
