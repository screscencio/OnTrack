package br.com.oncast.ontrack.client.services.applicationState;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn.VisibilityChangeListener;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ClientApplicationStateServiceImpl implements ClientApplicationStateService {

	private final EventBus eventBus;
	private final ContextProviderService contextProviderService;
	private final ClientStorageService clientStorageService;

	private final Set<HandlerRegistration> handlerRegistrations;

	private Scope selectedScope;

	public ClientApplicationStateServiceImpl(final EventBus eventBus, final ContextProviderService contextProviderService,
			final ClientStorageService clientStorageService) {
		this.eventBus = eventBus;
		this.contextProviderService = contextProviderService;
		this.clientStorageService = clientStorageService;
		handlerRegistrations = new HashSet<HandlerRegistration>();
	}

	@Override
	public void startRecording() {
		registerScopeSelectionEventListener();
		registerScopeTreeColumnVisibilityChangeListener();
	}

	private void registerScopeTreeColumnVisibilityChangeListener() {
		for (final ScopeTreeColumn column : ScopeTreeColumn.values()) {
			handlerRegistrations.add(column.register(new VisibilityChangeListener() {
				@Override
				public void onVisiblityChange(final boolean isVisible) {
					clientStorageService.storeScopeTreeColumnVisibility(column, isVisible);
				}
			}));
		}
	}

	private void registerScopeSelectionEventListener() {
		handlerRegistrations.add(eventBus.addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				setSelectedScope(event);
			}
		}));
	}

	@Override
	public void stopRecording() {
		for (final HandlerRegistration handlerRegistration : handlerRegistrations) {
			handlerRegistration.removeHandler();
		}

		handlerRegistrations.clear();
	}

	@Override
	public void restore() {
		fireScopeSelectionEvent();
		restoreScopeTreeColumnVisibility();
	}

	private void restoreScopeTreeColumnVisibility() {
		for (final ScopeTreeColumn column : ScopeTreeColumn.values()) {
			column.setVisibility(clientStorageService.loadScopeTreeColumnVisibility(column));
		}
	}

	private void fireScopeSelectionEvent() {
		Scheduler.get().scheduleEntry(new ScheduledCommand() {
			@Override
			public void execute() {
				eventBus.fireEvent(new ScopeSelectionEvent(getSelectedScope()));
			}
		});
	}

	private void setSelectedScope(final ScopeSelectionEvent event) {
		selectedScope = event.getTargetScope();
		storeSelectedScope();
	}

	private Scope getSelectedScope() {
		return selectedScope == null ? loadSelectedScope() : selectedScope;
	}

	private void storeSelectedScope() {
		clientStorageService.storeSelectedScopeId(selectedScope.getId());
	}

	private Scope loadSelectedScope() {
		final ProjectContext currentContext = contextProviderService.getCurrentProjectContext();
		try {
			return currentContext.findScope(clientStorageService.loadSelectedScopeId(currentContext.getProjectScope().getId()));
		}
		catch (final ScopeNotFoundException e) {
			return currentContext.getProjectScope();
		}
	}
}
