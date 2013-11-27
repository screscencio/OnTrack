package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "AnnotationVoteRemove")
@ConvertTo(AnnotationVoteRemoveAction.class)
public class AnnotationVoteRemoveActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String annotationId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String annotatedObjectId;

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
