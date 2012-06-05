package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;

@Entity(name = "AnnotationVote")
@ConvertTo(AnnotationVoteAction.class)
public class AnnotationVoteActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String annotationId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String annotatedObjectId;

	public String getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(String annotationId) {
		this.annotationId = annotationId;
	}

	public String getAnnotatedObjectId() {
		return annotatedObjectId;
	}

	public void setAnnotatedObjectId(String annotatedObjectId) {
		this.annotatedObjectId = annotatedObjectId;
	}

}
