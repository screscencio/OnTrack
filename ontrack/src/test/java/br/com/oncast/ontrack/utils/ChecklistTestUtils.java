package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistTestUtils {

	public static Checklist create() {
		final UUID uuid = new UUID();
		return new Checklist(uuid, "title " + uuid.toStringRepresentation());
	}

}
