package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.InformationBlockWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProgressBlockWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ScopeTimelineWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.TagAssociationWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.date.TimeDifferenceFormat;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareDueDateAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeDetailWidget extends Composite implements SubjectDetailWidget {

	private static ScopeDetailWidgetUiBinder uiBinder = GWT.create(ScopeDetailWidgetUiBinder.class);

	interface ScopeDetailWidgetUiBinder extends UiBinder<Widget, ScopeDetailWidget> {}

	public ScopeDetailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	ProgressBlockWidget effort;

	@UiField
	ProgressBlockWidget value;

	@UiField
	InformationBlockWidget cycletime;

	@UiField
	InformationBlockWidget leadtime;

	@UiField
	InformationBlockWidget dueDate;

	@UiField(provided = true)
	UserWidget ownerWidget;

	@UiField(provided = true)
	ScopeAssociatedMembersWidget associatedUsers;

	@UiField(provided = true)
	@IgnoredByDeepEquality
	protected ModelWidgetContainer<TagAssociationMetadata, TagAssociationWidget> tags;

	@UiField
	ScopeTimelineWidget timeline;

	private Scope scope;

	private ActionExecutionListener actionExecutionListener;

	public ScopeDetailWidget(final Scope scope) {
		associatedUsers = new ScopeAssociatedMembersWidget(scope, null, 10, true);
		tags = createTagsContainer();
		ownerWidget = new UserWidget(scope.getOwner());
		ownerWidget.setSmallSize();
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

	@UiHandler("dueDate")
	void onDueDateClick(final ClickEvent e) {
		final PopupConfig config = PopupConfig.configPopup();
		final DatePickerPopup pickerPopup = new DatePickerPopup(scope.getDueDate());
		pickerPopup.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Date> event) {
				ClientServices.get().actionExecution().onUserActionExecutionRequest(new ScopeDeclareDueDateAction(scope.getId(), event.getValue()));
				config.hidePopup();
			}
		});
		config.popup(pickerPopup).alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(dueDate, HorizontalAlignment.CENTER))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(dueDate, VerticalAlignment.BOTTOM, 3)).pop();
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
		return ClientServices.get().actionExecution();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
				final ModelAction action = execution.getModelAction();
				if ((action instanceof ScopeUpdateAction || action instanceof ScopeAddTagAssociationAction || action instanceof ScopeRemoveTagAssociationAction
						|| action instanceof ScopeBindReleaseAction || action instanceof ScopeRemoveAssociatedUserAction || action instanceof ScopeDeclareDueDateAction)
						&& (action.getReferenceId().equals(scope.getId()) || execution.getInferenceInfluencedScopeSet().contains(scope.getId()))) update();
				else if ((action instanceof ScopeDeclareProgressAction || action instanceof ImpedimentAction || action instanceof AnnotationCreateAction)
						&& action.getReferenceId().equals(scope.getId())) timeline.setScope(scope);
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		setTimeDifference(cycletime, scope.getProgress().getCycleTime());
		setTimeDifference(leadtime, scope.getProgress().getLeadTime());
		dueDate.setValue(scope.getDueDate());
		timeline.setScope(scope);

		this.effort.setValue(scope.getEffort().getAccomplished(), scope.getEffort().getInfered());
		this.value.setValue(scope.getValue().getAccomplished(), scope.getValue().getInfered());

		associatedUsers.update();
		tags.update(ClientServices.getCurrentProjectContext().<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType()));
	}

	private void setTimeDifference(final InformationBlockWidget widget, final Long difference) {
		if (difference == null) {
			widget.setAsNull();
			return;
		}

		final TimeDifferenceFormat format = HumanDateFormatter.get().setDecimalDigits(1).getTimeDifferenceFormat(difference);
		widget.setValue(format.getRawDateText());
		widget.setPosfix(format.getRwawUnitText());
	}

}
