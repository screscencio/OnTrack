package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationsWidget.UpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static final int TIME_REFRESH_INTERVAL = 60000;

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	interface AnnotationTopicStyle extends CssResource {
		public String likeActive();

		public String commentPanel();

		public String deprecatedIcon();

		public String deprecatedContainer();
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
	Label date;

	@UiField
	FocusPanel deprecate;

	@UiField
	Label likeCount;

	@UiField
	FocusPanel like;

	@UiField
	Label commentsCount;

	@UiField
	FocusPanel comment;

	@UiField
	SimplePanel commentsContainer;

	private Annotation annotation;

	private UUID subjectId;

	private boolean shouldRefresh = true;

	private AnnotationsWidget commentsPanel;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final Annotation annotation, final UUID subjectId, final boolean enableComments) {
		this.annotation = annotation;
		this.subjectId = subjectId;

		initWidget(uiBinder.createAndBindUi(this));

		deckPanel.showWidget(annotation.isDeprecated() ? 1 : 0);

		setupDeleteButton();
		setupContent();
		setupCommentsPanel(enableComments);
		update();
	}

	private void setupDeleteButton() {
		deprecate.setVisible(annotation.getAuthor().equals(getCurrentUser()));
	}

	@UiHandler("closedDeprecatedLabel")
	protected void onClosedDeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(0);
	}

	@UiHandler("deprecatedLabel")
	protected void ondeprecatedLabelClick(final ClickEvent e) {
		deckPanel.showWidget(1);
	}

	@UiHandler("deprecate")
	protected void onDeprecateClicked(final ClickEvent e) {
		if (annotation.isDeprecated()) getAnnotationService().removeDeprecation(subjectId, annotation.getId());
		else getAnnotationService().deprecateAnnotation(subjectId, annotation.getId());
	}

	@UiHandler("like")
	protected void onLikeClicked(final ClickEvent e) {
		if (annotation.hasVoted(getCurrentUser())) getAnnotationService().removeVote(subjectId, annotation.getId());
		else getAnnotationService().addVote(subjectId, annotation.getId());
	}

	@Override
	protected void onLoad() {
		shouldRefresh = true;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				updateTime();
				return shouldRefresh;
			}
		}, TIME_REFRESH_INTERVAL);
	}

	@Override
	protected void onUnload() {
		shouldRefresh = false;
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

	private void setupCommentsPanel(final boolean enableComments) {
		if (enableComments) {
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
			commentsPanel.setUpdateListener(new UpdateListener() {
				@Override
				public void onChanged() {
					updateComment();
				}
			});
			commentsPanel.setVisible(commentsPanel.getWidgetCount() > 0);
		}
		else {
			comment.setVisible(false);
			commentsCount.setVisible(false);
			commentsContainer.setVisible(false);
		}
	}

	private String getDateTimeText(final Annotation annotation) {
		return HumanDateFormatter.getRelativeDate(annotation.getCreationDate()) + " (" + HumanDateFormatter.getDifferenceDate(annotation.getCreationDate())
				+ ")";
	}

	@Override
	public boolean update() {
		updateLike();
		updateComment();
		updateTime();
		updateDeprecation();
		return false;
	}

	private void updateDeprecation() {
		final boolean isDeprecated = annotation.isDeprecated();

		deprecate.setTitle(isDeprecated ? "Remove Deprecation" : "Deprecate");

		deprecate.setStyleName(style.deprecatedIcon(), isDeprecated);
		container.setStyleName(style.deprecatedContainer(), isDeprecated);

		if (isDeprecated) {
			final String deprecationText = "[Deprecated by " + removeEmailDomain(annotation.getDeprecationAuthor(DeprecationState.DEPRECATED)) + " since "
					+ HumanDateFormatter.getRelativeDate(annotation.getDeprecationTimestamp(DeprecationState.DEPRECATED)) + "]";
			deprecatedLabel.setText(deprecationText);
			closedDeprecatedLabel.setText(deprecationText + " " + annotation.getMessage());

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

	private void updateComment() {
		if (commentsPanel == null) return;

		final String previousCount = this.commentsCount.getText();
		final String currentCount = "" + commentsPanel.getWidgetCount();
		this.commentsCount.setText(currentCount);
		if (currentCount.compareTo(previousCount) > 0 && !currentCount.equals("0")) commentsPanel.setVisible(true);

		this.comment.setTitle(commentsPanel.isVisible() ? "Hide Comments" : "Show Comments");
	}

	public void updateTime() {
		this.date.setText(getDateTimeText(annotation));
		this.date.setTitle(HumanDateFormatter.getAbsoluteText(annotation.getCreationDate()));
	}

	private void updateLike() {
		final boolean hasLiked = hasLiked();
		this.like.setStyleName(style.likeActive(), hasLiked);
		this.like.setTitle(hasLiked ? "Remove like" : "Like");

		this.likeCount.setText("" + annotation.getVoteCount());
	}

	private boolean hasLiked() {
		return annotation.hasVoted(getCurrentUser());
	}

	private AnnotationService getAnnotationService() {
		return ClientServiceProvider.getInstance().getAnnotationService();
	}

	private User getCurrentUser() {
		return ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
