package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class TagTestUtils {

	public static Tag create() {
		return new Tag(new UUID(), "description", Color.RED, Color.BLUE);
	}

}
