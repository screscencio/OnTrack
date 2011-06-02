package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite {

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	@UiField
	Label descriptionLabel;

	private final Scope scope;

	private String currentScopeDescription;

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		updateDescription();
	}

	public void update() {
		if (scope.getDescription().equals(currentScopeDescription)) return;
		updateDescription();
	}

	private void updateDescription() {
		currentScopeDescription = scope.getDescription();
		descriptionLabel.setText(currentScopeDescription);
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ScopeWidget)) return false;
		
		final ScopeWidget other = (ScopeWidget) obj;
		return scope.equals(other.getScope());
	}
}
