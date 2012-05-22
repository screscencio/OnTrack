package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;

@Entity(name = "ScopeBindRelease")
@ConvertTo(ScopeBindReleaseAction.class)
public class ScopeBindReleaseActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String newReleaseDescription;

	@ConversionAlias("subActionList")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "ScopeBindReleaseAction_subActionList")
	private List<ModelActionEntity> subActionList;

	@ConversionAlias("releaseCreateAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity secundarySubAction;

	@ConversionAlias("scopePriority")
	@Column(name = ActionTableColumns.INT_1)
	private int scopePriority;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

	public String getNewReleaseDescription() {
		return newReleaseDescription;
	}

	public void setNewReleaseDescription(final String newReleaseDescription) {
		this.newReleaseDescription = newReleaseDescription;
	}

	public void setScopePriority(final int scopePriority) {
		this.scopePriority = scopePriority;
	}

	public int getScopePriority() {
		return scopePriority;
	}

	public ModelActionEntity getSecundarySubAction() {
		return secundarySubAction;
	}

	public void setSecundarySubAction(final ModelActionEntity secundarySubAction) {
		this.secundarySubAction = secundarySubAction;
	}

}
