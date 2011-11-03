package br.com.oncast.ontrack.server.business;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class UserAction {

	@Attribute
	private long id;

	@Element
	private ModelAction action;

	@Attribute
	@IgnoredByDeepEquality
	private Date timestamp;

	public UserAction() {}

	public long getId() {
		return id;
	}

	public ModelAction getModelAction() {
		return action;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
