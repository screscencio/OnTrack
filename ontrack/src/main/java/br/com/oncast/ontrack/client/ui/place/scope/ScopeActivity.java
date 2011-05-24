package br.com.oncast.ontrack.client.ui.place.scope;

import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ScopeActivity extends AbstractActivity {

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ScopeView scopeView = new ScopeViewImpl();
		scopeView.setScope(getScope());
		panel.setWidget(scopeView);
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
