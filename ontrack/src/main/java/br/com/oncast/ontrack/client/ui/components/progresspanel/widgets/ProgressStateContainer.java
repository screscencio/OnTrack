package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProgressStateContainer extends Composite implements HasText {

	private static ProgressStateContainerUiBinder uiBinder = GWT.create(ProgressStateContainerUiBinder.class);

	interface ProgressStateContainerUiBinder extends UiBinder<Widget, ProgressStateContainer> {}

	public ProgressStateContainer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label title;

	@UiField
	ScopeWidgetContainer scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		scopeWidgetFactory = new ScopeWidgetFactory(new ProgressPanelWidgetInteractionHandler() {});
		return new ScopeWidgetContainer(scopeWidgetFactory, containerUpdateListener);
	}

	public ProgressStateContainer(final String text) {
		initWidget(uiBinder.createAndBindUi(this));
		this.title.setText(text);
	}

	@Override
	public String getText() {
		return title.getText();
	}

	@Override
	public void setText(final String text) {
		this.title.setText(text);
	}

	public void add(final Scope scope) {
		scopeContainer.createChildModelWidget(scope);
	}

}
