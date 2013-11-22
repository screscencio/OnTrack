package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public interface ModelAction extends Serializable, HasUUID {

	ModelAction execute(final ProjectContext context, ActionContext actionContext) throws UnableToCompleteActionException;

	UUID getReferenceId();

}
