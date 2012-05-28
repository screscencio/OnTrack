package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;

@Entity(name = "AnnotationCreate")
@ConvertTo(AnnotationCreateAction.class)
public class AnnotationCreateActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String annotationId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String annotatedObjectId;

	protected AnnotationCreateActionEntity() {}

	public String getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(final String annotationId) {
		this.annotationId = annotationId;
	}

	public String getAnnotatedObjectId() {
		return annotatedObjectId;
	}

	public void setAnnotatedObjectId(final String annotatedObjectId) {
		this.annotatedObjectId = annotatedObjectId;
	}

}
