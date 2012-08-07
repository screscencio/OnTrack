package br.com.oncast.ontrack.server.services.requestDispatch;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotatedSubjectIdsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotatedSubjectIdsResponse;

public class AnnotatedSubjectIdsRequestHandler implements RequestHandler<AnnotatedSubjectIdsRequest, AnnotatedSubjectIdsResponse> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public AnnotatedSubjectIdsResponse handle(final AnnotatedSubjectIdsRequest request) throws Exception {
		Set<UUID> annotatedSubjectIds = SERVICE_PROVIDER.getAnnotationBusinessLogic().retrieveAnnotatedSubjectIds(request.getProjectId());
		return new AnnotatedSubjectIdsResponse(annotatedSubjectIds);
	}

}
