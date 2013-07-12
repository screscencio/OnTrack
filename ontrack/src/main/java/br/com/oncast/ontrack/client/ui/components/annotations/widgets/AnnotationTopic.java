package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.CommentsAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.DeprecateAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.LikeAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.RemoveAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation>, ActionExecutionListener {

	private static final DetailPanelMessages messages = GWT.create(DetailPanelMessages.class);

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	interface AnnotationTopicStyle extends CssResource {
		String openImpediment();

		String solvedImpediment();

		String simple();

		String richTextArea();
	}

	@UiField
	AnnotationTopicStyle style;

	@UiField(provided = true)
	UserWidget author;

	@UiField
	Label deprecatedLabel;

	@UiField
	Label closedDeprecatedLabel;

	@UiField
	DeckPanel deckPanel;

	@UiField
	HorizontalPanel messageBody;

	@UiField
	AnnotationMenuWidget menu;

	@UiField(provided = true)
	CommentsWidget commentsPanel;

	@UiField
	HorizontalPanel content;

	private Annotation annotation;

	private UUID subjectId;

	private AnnotationType currentType;

	private CommentsAnnotationMenuItem commentsMenuItem;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		commentsPanel = new CommentsWidget(annotation.getId());
		author = new UserWidget(annotation.getAuthor()).showUserStatus(false);
		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.showWidget(annotation.isDeprecated() ? 1 : 0);

		updateMessageBody();
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(this);
		update();
	}

	@Override
	protected void onUnload() {
		ClientServices.get().actionExecution().removeActionExecutionListener(this);
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext, final boolean isUserAction) {
		if (action instanceof AnnotationAction && action.getReferenceId().equals(annotation.getId())) update();
	}

	@UiHandler("closedDeprecatedLabel")
	protected void onClosedDeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(0);
	}

	@UiHandler("deprecatedLabel")
	protected void onDeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(1);
	}

	private void updateMessageBody() {
		this.messageBody.clear();

		final FileRepresentation attachmentFile = annotation.getAttachmentFile();
		if (attachmentFile != null) {
			final AttachmentFileWidget attachedFileWidget = new AttachmentFileWidget(attachmentFile);
			messageBody.add(attachedFileWidget);
			messageBody.setCellHorizontalAlignment(attachedFileWidget, HorizontalPanel.ALIGN_CENTER);
		}

		if (this.annotation.getMessage().trim().isEmpty()) return;

		final InlineHTML richText = new InlineHTML();
		richText.setHTML(this.annotation.getMessage());
		richText.setStyleName(style.richTextArea());
		messageBody.add(richText);
		messageBody.setCellWidth(richText, "100%");
	}

	@Override
	public boolean update() {
		updateDeprecation();
		updateComments();

		if (currentType == null || currentType != annotation.getType()) {
			createCustomMenu();
		} else menu.update();
		return false;
	}

	private void createCustomMenu() {
		menu.clear();

		menu.add(new DeprecateAnnotationMenuItem(subjectId, annotation));
		menu.add(new RemoveAnnotationMenuItem(subjectId, annotation));

		final AnnotationTypeItemsMapper mapper = AnnotationTypeItemsMapper.get(annotation.getType());
		mapper.populateMenu(menu, subjectId, annotation);

		menu.addSeparator();
		menu.add(new LikeAnnotationMenuItem(subjectId, annotation));

		addCommentsMenuItem();
		menu.addSeparator();
		menu.add(mapper.getSinceWidget(annotation));

		content.setStyleName(mapper.getContentStyle(style));
		currentType = annotation.getType();
	}

	private void updateComments() {
		commentsPanel.setReadOnly(annotation.isDeprecated());
		commentsPanel.setVisible(commentsPanel.getWidgetCount() > 0);
		if (commentsMenuItem != null) commentsMenuItem.update();
	}

	private void addCommentsMenuItem() {
		commentsMenuItem = new CommentsAnnotationMenuItem(annotation);
		commentsMenuItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (annotation.isDeprecated() && commentsPanel.getWidgetCount() == 0) return;

				commentsPanel.setVisible(!commentsPanel.isVisible());
				commentsPanel.setFocus(true);
			}
		});
		menu.add(commentsMenuItem);
	}

	private void updateDeprecation() {
		final boolean isDeprecated = annotation.isDeprecated();

		if (isDeprecated) updateDeprecatedLabels();

		deprecatedLabel.setVisible(isDeprecated);
	}

	private void updateDeprecatedLabels() {
		final String absoluteDate = HumanDateFormatter.formatAbsoluteDate(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));

		updateDeprecationLabel();
		deprecatedLabel.setTitle(absoluteDate);

		closedDeprecatedLabel.setText(messages.deprecated(annotation.getMessage()));
		closedDeprecatedLabel.setTitle(absoluteDate);
	}

	private void updateDeprecationLabel() {
		final String formattedDate = HumanDateFormatter.get().formatDateRelativeToNow(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));
		deprecatedLabel.setText(messages.deprecationDetails("user", formattedDate));
		ClientServices.get().userData().loadRealUser(annotation.getDeprecationAuthor(DeprecationState.DEPRECATED).getId(), new AsyncCallback<User>() {

			@Override
			public void onSuccess(final User result) {
				deprecatedLabel.setText(messages.deprecationDetails(result.getName(), formattedDate));
			}

			@Override
			public void onFailure(final Throwable caught) {

			}
		});
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
