package br.com.oncast.ontrack.client.ui.component.scopetree.widgets.tags;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseTag extends Composite {

	private static ReleaseTagUiBinder uiBinder = GWT.create(ReleaseTagUiBinder.class);

	interface ReleaseTagUiBinder extends UiBinder<Widget, ReleaseTag> {}

	public ReleaseTag() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ReleaseTag(final String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
