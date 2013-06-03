package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.LikeAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.RemoveAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.SinceAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationComment extends Composite implements ModelWidget<Annotation> {

	private static final DetailPanelMessages MESSAGES = GWT.create(DetailPanelMessages.class);

	private static AnnotationCommentUiBinder uiBinder = GWT.create(AnnotationCommentUiBinder.class);

	interface AnnotationCommentUiBinder extends UiBinder<Widget, AnnotationComment> {}

	@UiField(provided = true)
	UserWidget author;

	@UiField
	Label deprecatedLabel;

	@UiField
	Label closedDeprecatedLabel;

	@UiField
	DeckPanel deckPanel;

	@UiField
	HTMLPanel messageBody;

	@UiField
	AnnotationMenuWidget menu;

	private Annotation annotation;

	private UUID subjectId;

	public AnnotationComment() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationComment(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		author = new UserWidget(annotation.getAuthor());

		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.showWidget(annotation.isDeprecated() ? 1 : 0);

		updateMessageBody();
		updateMenu();

		update();
	}

	@UiHandler("closedDeprecatedLabel")
	protected void onClosedDeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(0);
	}

	@UiHandler("deprecatedLabel")
	protected void ondeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(1);
	}

	private void updateMessageBody() {
		this.messageBody.clear();

		final FileRepresentation attachmentFile = annotation.getAttachmentFile();
		if (attachmentFile != null) {
			final AttachmentFileWidget attachedFileWidget = new AttachmentFileWidget(attachmentFile);
			messageBody.add(attachedFileWidget);
		}

		for (final String line : this.annotation.getMessage().split("\\n")) {
			messageBody.add(new HTMLPanel(SimpleHtmlSanitizer.sanitizeHtml(line)));
		}
	}

	@Override
	public boolean update() {
		updateDeprecation();

		menu.update();
		return false;
	}

	private void updateMenu() {
		menu.clear();

		menu.add(new RemoveAnnotationMenuItem(subjectId, annotation));
		menu.add(new LikeAnnotationMenuItem(subjectId, annotation));
		menu.addSeparator();
		menu.add(new SinceAnnotationMenuItem(annotation));
	}

	private void updateDeprecation() {
		final boolean isDeprecated = annotation.isDeprecated();

		if (isDeprecated) updateDeprecatedLabels();

		deprecatedLabel.setVisible(isDeprecated);

	}

	private void updateDeprecatedLabels() {
		final String absoluteDate = HumanDateFormatter.formatAbsoluteDate(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));

		deprecatedLabel.setText(getDeprecationText());
		deprecatedLabel.setTitle(absoluteDate);

		closedDeprecatedLabel.setText(MESSAGES.deprecated(annotation.getMessage()));
		closedDeprecatedLabel.setTitle(absoluteDate);
	}

	private String getDeprecationText() {
		final User user = ClientServices.get().userData()
				.getRealUser(annotation.getDeprecationAuthor(DeprecationState.DEPRECATED));
		final String formattedDate = HumanDateFormatter.get().formatDateRelativeToNow(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));

		return MESSAGES.deprecationDetails(user.getName(), formattedDate);
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

	public void setReadOnly(final boolean b) {
		menu.setReadOnly(b);
	}

}
