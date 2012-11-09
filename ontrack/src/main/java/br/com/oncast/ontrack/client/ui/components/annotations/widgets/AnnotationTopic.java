package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.CommentsAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.DeprecateAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.LikeAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static final DetailPanelMessages messages = GWT.create(DetailPanelMessages.class);

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	interface AnnotationTopicStyle extends CssResource {
		String openImpediment();

		String solvedImpediment();

		String simple();
	}

	@UiField
	AnnotationTopicStyle style;

	@UiField
	Image author;

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

	@UiField(provided = true)
	CommentsWidget commentsPanel;

	@UiField
	HorizontalPanel content;

	private Annotation annotation;

	private UUID subjectId;

	private AnnotationType currentType;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		commentsPanel = new CommentsWidget(annotation.getId());
		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.showWidget(annotation.isDeprecated() ? 1 : 0);

		updateAuthorImage();
		updateMessageBody();
	}

	@Override
	protected void onLoad() {
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

	private void updateAuthorImage() {
		final String email = this.annotation.getAuthor().getEmail();
		this.author.setUrl(ClientServiceProvider.getInstance().getUserDataService().getAvatarUrl(email));

		ClientServiceProvider.getInstance().getUserDataService().loadProfile(email, new LoadProfileCallback() {
			@Override
			public void onProfileLoaded(final PortableContactJsonObject profile) {
				author.setTitle(profile.getPreferedUsername());
			}

			@Override
			public void onProfileUnavailable(final Throwable cause) {
				author.setTitle(email);
			}
		});
	}

	@Override
	public boolean update() {
		updateDeprecation();
		updateComments();

		if (currentType == null || currentType != annotation.getType()) {
			createCustomMenu();
		}
		else menu.update();
		return false;
	}

	private void createCustomMenu() {
		menu.clear();

		menu.add(new DeprecateAnnotationMenuItem(subjectId, annotation));

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
	}

	private void addCommentsMenuItem() {
		final CommentsAnnotationMenuItem comment = new CommentsAnnotationMenuItem(annotation);
		comment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (annotation.isDeprecated() && commentsPanel.getWidgetCount() == 0) return;

				commentsPanel.setVisible(!commentsPanel.isVisible());
				commentsPanel.setFocus(true);
			}
		});
		menu.add(comment);
	}

	private void updateDeprecation() {
		final boolean isDeprecated = annotation.isDeprecated();

		if (isDeprecated) updateDeprecatedLabels();

		deprecatedLabel.setVisible(isDeprecated);
	}

	private void updateDeprecatedLabels() {
		final String absoluteDate = HumanDateFormatter.getAbsoluteText(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));

		deprecatedLabel.setText(getDeprecationText());
		deprecatedLabel.setTitle(absoluteDate);

		closedDeprecatedLabel.setText(messages.deprecated(annotation.getMessage()));
		closedDeprecatedLabel.setTitle(absoluteDate);
	}

	private String getDeprecationText() {
		final String username = removeEmailDomain(annotation.getDeprecationAuthor(DeprecationState.DEPRECATED));
		final String formattedDate = HumanDateFormatter.getRelativeDate(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));
		return messages.deprecationDetails(username, formattedDate);
	}

	private String removeEmailDomain(final User user) {
		return user.getEmail().replaceAll("@.*$", "");
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
