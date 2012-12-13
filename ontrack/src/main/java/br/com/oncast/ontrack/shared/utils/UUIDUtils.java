package br.com.oncast.ontrack.shared.utils;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UUIDUtils {

	public static boolean equals(final Object obj1, final Object obj2) {
		if (obj1 != null && obj1 == obj2) return true;

		final UUID id1 = extractId(obj1);
		final UUID id2 = extractId(obj2);

		if (id1 == null) return false;
		return id1.equals(id2);
	}

	public static int hashCode(final Object obj) {
		return extractId(obj).hashCode();
	}

	private static UUID extractId(final Object obj) {
		if (obj instanceof UUID) return (UUID) obj;
		if (obj instanceof HasUUID) return ((HasUUID) obj).getId();
		return null;
	}

}
