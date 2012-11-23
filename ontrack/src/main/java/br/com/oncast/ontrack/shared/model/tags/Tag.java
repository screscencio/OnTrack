package br.com.oncast.ontrack.shared.model.tags;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface Tag {

	HasTags getSubject();

	TagType getTagType();

	UUID getId();

}
