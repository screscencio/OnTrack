package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface DescriptionWidgetMessages extends BaseMessages {

	@Description("description widget description message")
	@DefaultMessage("Description of ''{0}''")
	String descriptionOf(String description);
}
