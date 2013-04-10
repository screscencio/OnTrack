package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeAssociatedTagsWidget extends Composite implements ActionExecutionListener {

	private static ScopeAssociatedTagsWidgetUiBinder uiBinder = GWT.create(ScopeAssociatedTagsWidgetUiBinder.class);

	interface ScopeAssociatedTagsWidgetUiBinder extends UiBinder<Widget, ScopeAssociatedTagsWidget> {}

	public ScopeAssociatedTagsWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField(provided = true)
	ModelWidgetContainer<Tag, ScopeTagWidget> tags;

	private Scope scope;

	public ScopeAssociatedTagsWidget(final Scope scope) {
		this.scope = scope;
		tags = createContainer();
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public void update() {
		tags.update(ClientServices.getCurrentProjectContext().getTagsFor(scope));
	}

	private ModelWidgetContainer<Tag, ScopeTagWidget> createContainer() {
		return new ModelWidgetContainer<Tag, ScopeTagWidget>(new ModelWidgetFactory<Tag, ScopeTagWidget>() {
			@Override
			public ScopeTagWidget createWidget(final Tag tag) {
				return new ScopeTagWidget(tag);
			}
		}, new AnimatedContainer(new HorizontalPanel()));
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet,
			final boolean isUserAction) {
		if ((action instanceof ScopeAddTagAssociationAction || action instanceof ScopeRemoveTagAssociationAction)
				&& action.getReferenceId().equals(scope.getId())) {
			update();
		}
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServices.get().actionExecution().removeActionExecutionListener(this);
	}

}
