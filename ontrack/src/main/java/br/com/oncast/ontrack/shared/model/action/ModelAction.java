package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;

@XmlRootElement
public interface ModelAction extends Serializable, JsonSerializable {

	ModelAction execute(final ProjectContext context, ActionContext actionContext) throws UnableToCompleteActionException;

	UUID getReferenceId();

}
