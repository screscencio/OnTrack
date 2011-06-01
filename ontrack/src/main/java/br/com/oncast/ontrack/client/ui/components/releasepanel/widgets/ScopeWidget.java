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

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		descriptionLabel.setText(scope.getDescription());
	}

	public Scope getScope() {
		return scope;
	}
}
