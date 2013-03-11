package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ReleaseDetailWidgetMessages extends BaseMessages {

	@DefaultMessage("None")
	@Description("shown when the release has no parent")
	String none();

	@DefaultMessage("day")
	@Description("day")
	String day();

}
