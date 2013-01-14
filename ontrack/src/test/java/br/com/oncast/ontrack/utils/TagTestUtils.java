package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class TagTestUtils {

	public static Tag createTag() {
		return new Tag(new UUID(), "description", new ColorPack(Color.BLUE, Color.RED));
	}

}
