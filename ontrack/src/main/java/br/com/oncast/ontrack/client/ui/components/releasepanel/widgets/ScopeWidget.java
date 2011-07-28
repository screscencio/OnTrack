package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite implements ModelWidget<Scope> {

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	@UiField
	Label descriptionLabel;

	@UiField
	Label progressLabel;

	private final Scope scope;

	private String currentScopeDescription;

	private String currentScopeProgress;

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		updateDescription();
		updateProgress();
	}

	@Override
	public void update() {
		updateDescription();
		updateProgress();
	}

	private void updateDescription() {
		final String description = scope.getDescription();
		if (description.equals(currentScopeDescription)) return;
		currentScopeDescription = description;

		descriptionLabel.setText(currentScopeDescription);
	}

	private void updateProgress() {
		final String description = scope.getProgress().getDescription();
		if (description.equals(currentScopeProgress)) return;
		currentScopeProgress = description;

		progressLabel.setText(currentScopeProgress);
		progressLabel.setVisible(!currentScopeProgress.isEmpty());
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}
}
