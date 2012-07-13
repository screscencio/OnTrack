package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
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

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String attachmentId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String message;

	@ConversionAlias("subActions")
	@Column(name = ActionTableColumns.ACTION_LIST)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "AnnotationCreate_subActionList")
	private List<ModelActionEntity> subActionList;

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

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(final String attachmentFileId) {
		this.attachmentId = attachmentFileId;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

}
