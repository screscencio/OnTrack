package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildRollbackAction;

@Entity
@ConvertTo(ScopeInsertChildRollbackAction.class)
public class ScopeInsertChildRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "modelActionEntity_subActionList")
	@JoinTable(name = "ScopeInsertChildRollbackAction_SubActionList")
	private List<ModelActionEntity> subActionList;

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

}
