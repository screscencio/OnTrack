package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.progress.Progress;
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
		if (scope.getDescription().equals(currentScopeDescription)) return;
		currentScopeDescription = scope.getDescription();
		descriptionLabel.setText(currentScopeDescription);
	}

	private void updateProgress() {
		if (scope.getProgress().getDescription().equals(currentScopeProgress)) return;
		currentScopeProgress = getProgressDescription();
		progressLabel.setText(currentScopeProgress);
		progressLabel.setVisible(!currentScopeProgress.isEmpty());
	}

	private String getProgressDescription() {
		return scope.getProgress().getStatus() == Progress.STATUS.UNDER_WORK ? scope.getProgress().getDescription() : scope.getProgress().getStatus()
				.toString();
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}
}
