package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import static br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat.roundFloat;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.TagAssociationWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ScopeDetailWidget extends Composite implements SubjectDetailWidget {

	private static final ScopeDetailWidgetMessages messages = GWT.create(ScopeDetailWidgetMessages.class);

	private static ScopeDetailWidgetUiBinder uiBinder = GWT.create(ScopeDetailWidgetUiBinder.class);

	interface ScopeDetailWidgetUiBinder extends UiBinder<Widget, ScopeDetailWidget> {}

	public ScopeDetailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HasText parent;

	@UiField
	HasText humanId;

	@UiField
	HasText effort;

	@UiField
	HasText value;

	@UiField
	HasText progress;

	@UiField
	HasText release;

	@UiField(provided = true)
	UserWidget ownerWidget;

	@UiField(provided = true)
	ScopeAssociatedMembersWidget associatedUsers;

	@UiField(provided = true)
	@IgnoredByDeepEquality
	protected ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget> tags;

	private Scope scope;

	private ActionExecutionListener actionExecutionListener;

	public ScopeDetailWidget(final Scope scope) {
		associatedUsers = new ScopeAssociatedMembersWidget(scope, null, 10, true);
		tags = createTagsContainer();
		ownerWidget = new UserWidget(scope.getOwner());
		initWidget(uiBinder.createAndBindUi(this));
		setSubject(scope);
		associatedUsers.getElement().getParentElement().setAttribute("colspan", "2");
	}

	private ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget> createTagsContainer() {
		return new ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget>(new ModelWidgetFactory<TagAssociationMetadata, TagAssociationWidget>() {
			@Override
			public TagAssociationWidget createWidget(final TagAssociationMetadata modelBean) {
				return new TagAssociationWidget(modelBean);
			}
		}, new AnimatedContainer(new FlowPanel()));
	}

	private void setSubject(final Scope scope) {
		this.scope = scope;
		associatedUsers.setScope(scope);
		update();
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ScopeUpdateAction && action.getReferenceId().equals(scope.getId())) update();
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		this.parent.setText(scope.isRoot() ? messages.none() : scope.getParent().getDescription());
		this.humanId.setText(ClientServiceProvider.getCurrentProjectContext().getHumanId(scope));
		this.effort.setText(formatProgressText(scope.getEffort().getAccomplished(), scope.getEffort().getInfered(), " ep"));
		this.value.setText(formatProgressText(scope.getValue().getAccomplished(), scope.getValue().getInfered(), " vp"));
		final String progress = scope.getProgress().getDescription();
		this.progress.setText(progress.isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : progress);
		final Release release = scope.getRelease();
		this.release.setText(release == null ? messages.none() : release.getDescription());
		associatedUsers.update();
		tags.update(ClientServiceProvider.getCurrentProjectContext().<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType()));
	}

	private String formatProgressText(final float accomplished, final float total, final String unit) {
		if (total == 0) return format(total) + unit;
		final String percentage = accomplished == 0 ? "" : (" ( " + format(accomplished * 100 / total) + "% )");
		return format(accomplished) + " / " + format(total) + unit + percentage;
	}

	private String format(final float floatValue) {
		return roundFloat(floatValue, 0);
	}

}
