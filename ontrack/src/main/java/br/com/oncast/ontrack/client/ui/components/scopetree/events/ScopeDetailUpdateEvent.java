package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.releasepanel.events.AbstractSubjectDetailUpdateEvent;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeDetailUpdateEvent extends AbstractSubjectDetailUpdateEvent<Scope, ScopeDetailUpdateEventHandler> {

	public static Type<ScopeDetailUpdateEventHandler> TYPE;

	public ScopeDetailUpdateEvent(final Scope subject, final List<Checklist> checklists, final List<Annotation> annotations, final Description description) {
		super(subject, checklists, annotations, description);
	}

	public ScopeDetailUpdateEvent(final Scope subject) {
		super(subject);
	}

	public static Type<ScopeDetailUpdateEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeDetailUpdateEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeDetailUpdateEventHandler> getAssociatedType() {
		return ScopeDetailUpdateEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeDetailUpdateEventHandler handler) {
		handler.onScopeDetailUpdate(this);
	}

}
