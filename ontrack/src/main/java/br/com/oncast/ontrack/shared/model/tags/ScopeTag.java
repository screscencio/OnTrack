package br.com.oncast.ontrack.shared.model.tags;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeTag implements Tag, Serializable {

	private static final long serialVersionUID = 1L;

	private final Scope scope;
	private final UUID tagId;

	private final String description;

	public ScopeTag(final UUID tagId, final Scope scope, final String description) {
		this.tagId = tagId;
		this.scope = scope;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public UUID getId() {
		return tagId;
	}

	@Override
	public HasTags getSubject() {
		return scope;
	}

	@Override
	public TagType getTagType() {
		return TagType.TAG;
	}

}
