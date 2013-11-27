package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotatedSubjectIdsRequest implements DispatchRequest<AnnotatedSubjectIdsResponse> {

	private UUID projectId;

	AnnotatedSubjectIdsRequest() {}

	public AnnotatedSubjectIdsRequest(final UUID projectId) {
		this.projectId = projectId;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
