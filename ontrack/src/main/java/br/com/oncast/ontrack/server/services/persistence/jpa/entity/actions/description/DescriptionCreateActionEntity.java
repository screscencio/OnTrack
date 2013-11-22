package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.DescriptionCreateAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "DescriptionCreate")
@ConvertTo(DescriptionCreateAction.class)
public class DescriptionCreateActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String descriptionId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String subjectId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String description;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	protected DescriptionCreateActionEntity() {}

	public String getDescriptionId() {
		return descriptionId;
	}

	public void setDescriptionId(final String descriptionId) {
		this.descriptionId = descriptionId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final String subjectId) {
		this.subjectId = subjectId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
}
