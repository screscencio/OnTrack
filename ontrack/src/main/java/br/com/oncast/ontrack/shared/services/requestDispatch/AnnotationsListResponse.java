package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

public class AnnotationsListResponse implements DispatchResponse {

	private List<Annotation> annotationsList;

	AnnotationsListResponse() {}

	public AnnotationsListResponse(final List<Annotation> annotationsList) {
		this.annotationsList = annotationsList;
	}

	public List<Annotation> getAnnotationsList() {
		return annotationsList;
	}

}
