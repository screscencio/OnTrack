package br.com.oncast.ontrack.server.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotationsListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotationsListResponse;

public class AnnotationsListRequestHandler implements RequestHandler<AnnotationsListRequest, AnnotationsListResponse> {

	@Override
	public AnnotationsListResponse handle(final AnnotationsListRequest request) throws Exception {
		final List<Annotation> annotationsList = ServerServiceProvider.getInstance().getAnnotationBusinessLogic()
				.retrieveAnnotationsListFor(request.getProjectId(), request.getSubjectId());
		return new AnnotationsListResponse(annotationsList);
	}
}
