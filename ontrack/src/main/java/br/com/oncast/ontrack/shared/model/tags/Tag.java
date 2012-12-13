package br.com.oncast.ontrack.shared.model.tags;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;

public interface Tag extends HasUUID {

	HasTags getSubject();

	TagType getTagType();

}
