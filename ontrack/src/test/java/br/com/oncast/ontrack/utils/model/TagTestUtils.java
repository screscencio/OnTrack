package br.com.oncast.ontrack.utils.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import br.com.oncast.ontrack.shared.model.tags.HasTags;
import br.com.oncast.ontrack.shared.model.tags.Tag;
import br.com.oncast.ontrack.shared.model.tags.TagType;
import br.com.oncast.ontrack.shared.model.tags.UserTag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class TagTestUtils {

	public static Tag create() {
		return create(new UUID(), new UUID(), UserTag.getType());
	}

	public static Tag create(final UUID subjectId, final UUID tagId, final TagType type) {
		final Tag tag = mock(Tag.class);
		final HasTags subject = mock(HasTags.class);
		when(subject.getId()).thenReturn(subjectId);

		when(tag.getSubject()).thenReturn(subject);
		when(tag.getId()).thenReturn(tagId);
		when(tag.getTagType()).thenReturn(type);

		return tag;
	}

}
