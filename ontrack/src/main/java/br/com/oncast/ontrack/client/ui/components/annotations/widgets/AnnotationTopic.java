package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationDeprecateWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.CommentsWidget;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	interface AnnotationTopicStyle extends CssResource {
		String likeActive();

		String commentPanel();

		String deprecatedIcon();

		String deprecatedContainer();

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
	HTMLPanel container;

	@UiField
	AnnotationMenuWidget menu;

	@UiField
	SimplePanel commentsContainer;

	@UiField
	HorizontalPanel content;

	private Annotation annotation;

	private AnnotationsWidget commentsPanel;

	private UUID subjectId;

	private AnnotationType currentType;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.showWidget(annotation.isDeprecated() ? 1 : 0);

		setupContent();
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

	private void setupContent() {
		final String email = this.annotation.getAuthor().getEmail();
		this.container.clear();
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

		final FileRepresentation attachmentFile = annotation.getAttachmentFile();
		if (attachmentFile != null) {
			final AttachmentFileWidget attachedFileWidget = new AttachmentFileWidget(attachmentFile);
			container.add(attachedFileWidget);
		}

		for (final String line : this.annotation.getMessage().split("\\n")) {
			container.add(new HTMLPanel(SimpleHtmlSanitizer.sanitizeHtml(line)));
		}
	}

	@Override
	public boolean update() {
		updateDeprecation();
		if (currentType == null || currentType != annotation.getType()) {
			menu.clear();
			menu.add(new AnnotationDeprecateWidget(subjectId, annotation));
			setupCommentsPanel();

			final AnnotationTypeItemsMapper mapper = AnnotationTypeItemsMapper.get(annotation.getType());
			mapper.populateMenu(menu, subjectId, annotation);

			content.setStyleName(mapper.getContentStyle(style));
			currentType = annotation.getType();
		}
		else menu.update();
		return false;
	}

	private void setupCommentsPanel() {
		if (annotation.getType().acceptsComments()) {
			final CommentsWidget comment = new CommentsWidget(annotation);
			menu.add(comment);
			commentsPanel = AnnotationsWidget.forComments(annotation.getId());
			commentsPanel.addStyleName(style.commentPanel());
			commentsContainer.add(commentsPanel);
			comment.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					commentsPanel.setVisible(!commentsPanel.isVisible());
					commentsPanel.setFocus(true);
				}
			});
			commentsPanel.setVisible(commentsPanel.getWidgetCount() > 0);
		}
		else {
			commentsContainer.setVisible(false);
		}
	}

	private void updateDeprecation() {
		final boolean isDeprecated = annotation.isDeprecated();

		container.setStyleName(style.deprecatedContainer(), isDeprecated);

		if (isDeprecated) {
			final String deprecationText = "[Deprecated by " + removeEmailDomain(annotation.getDeprecationAuthor(DeprecationState.DEPRECATED)) + " since "
					+ HumanDateFormatter.getRelativeDate(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED)) + "]";
			deprecatedLabel.setText(deprecationText);
			closedDeprecatedLabel.setText("[Deprecated] " + annotation.getMessage());

			final String absoluteDate = HumanDateFormatter.getAbsoluteText(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED));
			deprecatedLabel.setTitle(absoluteDate);
			closedDeprecatedLabel.setTitle(absoluteDate);
		}

		if (commentsPanel != null) commentsPanel.setReadOnly(isDeprecated);

		deprecatedLabel.setVisible(isDeprecated);
	}

	private String removeEmailDomain(final User user) {
		return user.getEmail().replaceAll("@.*$", "");
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
