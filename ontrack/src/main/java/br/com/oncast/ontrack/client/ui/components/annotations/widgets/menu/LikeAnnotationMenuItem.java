package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LikeAnnotationMenuItem extends Composite implements AnnotationMenuItem {

	private static LikeAnnotationMenuItemUiBinder uiBinder = GWT.create(LikeAnnotationMenuItemUiBinder.class);

	private static final LikeAnnotationMenuItemMessages messages = GWT.create(LikeAnnotationMenuItemMessages.class);

	interface LikeAnnotationMenuItemUiBinder extends UiBinder<Widget, LikeAnnotationMenuItem> {}

	interface LikeWidgetStyle extends CssResource {
		String iconActive();
	}

	@UiField
	FocusPanel icon;

	@UiField
	Label label;

	@UiField
	LikeWidgetStyle style;

	private final UUID subjectId;

	private final Annotation annotation;

	private boolean readOnly = false;

	public LikeAnnotationMenuItem(final UUID subjectId, final Annotation annotation) {
		this.subjectId = subjectId;
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));

		update();
	}

	@Override
	public void update() {
		label.setText("" + annotation.getVoteCount());
		final boolean hasVoted = hasVoted();
		icon.setStyleName(style.iconActive(), hasVoted);
		icon.setTitle(hasVoted ? messages.removeLike() : messages.like());
	}

	private boolean hasVoted() {
		return annotation.hasVoted(ClientServiceProvider.getCurrentUser());
	}

	@UiHandler("icon")
	void onClick(final ClickEvent e) {
		if (readOnly || annotation.isDeprecated()) return;

		if (hasVoted()) ClientServiceProvider.getInstance().getAnnotationService().removeVote(subjectId, annotation.getId());
		else ClientServiceProvider.getInstance().getAnnotationService().addVote(subjectId, annotation.getId());
	}

	@Override
	public void setReadOnly(final boolean b) {
		this.readOnly = b;
	}

}
