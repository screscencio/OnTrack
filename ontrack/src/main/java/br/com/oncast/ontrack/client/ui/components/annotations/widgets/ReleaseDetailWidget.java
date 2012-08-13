package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseDetailWidget extends Composite implements SubjectDetailWidget {

	private static ReleaseDetailWidgetUiBinder uiBinder = GWT.create(ReleaseDetailWidgetUiBinder.class);

	interface ReleaseDetailWidgetUiBinder extends UiBinder<Widget, ReleaseDetailWidget> {}

	public ReleaseDetailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HasText parent;

	@UiField
	HasText effort;

	@UiField
	HasText accomplishedEffort;

	@UiField
	HasText value;

	private Release release;

	private ActionExecutionListener actionExecutionListener;

	public ReleaseDetailWidget(final Release release) {
		this();
		setSubject(release);
	}

	private void setSubject(final Release release) {
		this.release = release;
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
			public void onActionExecution(final ModelAction action, final ProjectContext context, ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ReleaseRenameAction && action.getReferenceId().equals(release.getId())) update();
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		this.parent.setText(release.getParent().isRoot() ? "None" : release.getParent().getDescription());
		this.effort.setText(format(release.getEffortSum(), " ep"));
		this.accomplishedEffort.setText(format(release.getAccomplishedEffortSum(), " ep"));
		this.value.setText(format(release.getValueSum(), " vp"));
	}

	private String format(final float floatValue, final String posfix) {
		return ClientDecimalFormat.roundFloat(floatValue, 1) + posfix;
	}

}
