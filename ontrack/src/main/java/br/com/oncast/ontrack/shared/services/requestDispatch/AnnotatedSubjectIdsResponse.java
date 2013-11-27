package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.Set;

public class AnnotatedSubjectIdsResponse implements DispatchResponse {

	private Set<UUID> annotatedSubjectIds;

	AnnotatedSubjectIdsResponse() {}

	public AnnotatedSubjectIdsResponse(final Set<UUID> annotatedSubjectIds) {
		this.annotatedSubjectIds = annotatedSubjectIds;
	}

	public Set<UUID> getAnnotatedSubjectIds() {
		return annotatedSubjectIds;
	}

}
