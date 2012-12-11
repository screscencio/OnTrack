package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface DescriptionAction extends ModelAction {
	UUID getDescriptionId();

	String getDescription();
}
