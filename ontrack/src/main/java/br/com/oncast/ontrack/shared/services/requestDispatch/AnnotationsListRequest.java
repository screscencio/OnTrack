package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationsListRequest implements DispatchRequest<AnnotationsListResponse> {

	private UUID projectId;
	private UUID subjectId;

	AnnotationsListRequest() {}

	public AnnotationsListRequest(final UUID projectId, final UUID subjectId) {
		this.projectId = projectId;
		this.subjectId = subjectId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public UUID getSubjectId() {
		return subjectId;
	}

}
