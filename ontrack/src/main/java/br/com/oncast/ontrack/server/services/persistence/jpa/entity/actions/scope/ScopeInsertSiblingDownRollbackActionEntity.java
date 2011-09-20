package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownRollbackAction;

@Entity
@ConvertTo(ScopeInsertSiblingDownRollbackAction.class)
public class ScopeInsertSiblingDownRollbackActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String referenceId;

	@ConversionAlias("scopeUpdateRollbackAction")
	@OneToOne(cascade = CascadeType.ALL)
	private ModelActionEntity subAction;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public ModelActionEntity getSubAction() {
		return subAction;
	}

	public void setSubAction(final ModelActionEntity subAction) {
		this.subAction = subAction;
	}
}
