package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignedToMeWidget extends Composite implements PopupAware, HasCloseHandlers<AssignedToMeWidget> {

	private static AssignedToMeWidgetUiBinder uiBinder = GWT.create(AssignedToMeWidgetUiBinder.class);

	interface AssignedToMeWidgetUiBinder extends UiBinder<Widget, AssignedToMeWidget> {}

	@UiField
	DeckPanel deck;

	@UiField(provided = true)
	ModelWidgetContainer<Scope, AssignedScopeWidget> assignedScopesContainer;

	public AssignedToMeWidget() {
		assignedScopesContainer = createAssignedScopesContainer();

		initWidget(uiBinder.createAndBindUi(this));
	}

	private ModelWidgetContainer<Scope, AssignedScopeWidget> createAssignedScopesContainer() {
		return new ModelWidgetContainer<Scope, AssignedScopeWidget>(new ModelWidgetFactory<Scope, AssignedScopeWidget>() {
			@Override
			public AssignedScopeWidget createWidget(final Scope modelBean) {
				final AssignedScopeWidget assignedScopeWidget = new AssignedScopeWidget(modelBean);
				assignedScopeWidget.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						ClientServices.get().eventBus().fireEventFromSource(new ScopeSelectionEvent(modelBean), this);
						hide();
					}
				});
				return assignedScopeWidget;
			}
		}, new AnimatedContainer(new VerticalPanel()));
	}

	private void update() {
		final ProjectContext context = ClientServices.getCurrentProjectContext();

		final List<Scope> assignedScopes = getAssignedScopes(context);
		sortByReleasePriority(context, assignedScopes);

		assignedScopesContainer.update(assignedScopes);
		deck.showWidget(assignedScopes.isEmpty() ? 0 : 1);
	}

	private List<Scope> getAssignedScopes(final ProjectContext context) {
		final UUID userId = ClientServices.getCurrentUser();

		final List<Scope> assignedScopes = new ArrayList<Scope>();
		for (final UserAssociationMetadata m : context.<UserAssociationMetadata> getAllMetadata(MetadataType.USER)) {
			if (!m.getUser().getId().equals(userId) || !(m.getSubject() instanceof Scope)) continue;

			final Scope subject = (Scope) m.getSubject();
			if (!subject.getProgress().isDone()) assignedScopes.add(subject);
		}
		return assignedScopes;
	}

	private void sortByReleasePriority(final ProjectContext context, final List<Scope> assignedScopes) {
		final List<Scope> allStoriesInTemporalOrder = context.getProjectRelease().getAllStoriesInTemporalOrderIncludingDescendantReleases();
		final List<Scope> allScopes = context.getProjectScope().getAllDescendantScopes();
		Collections.sort(assignedScopes, new Comparator<Scope>() {
			@Override
			public int compare(final Scope o1, final Scope o2) {
				final int result = allStoriesInTemporalOrder.indexOf(o1.getStory()) - allStoriesInTemporalOrder.indexOf(o2.getStory());
				if (result != 0) return result;

				return allScopes.indexOf(o1) - allScopes.indexOf(o2);
			}

		});
	}

	@Override
	public void show() {
		update();
	}

	@Override
	public void hide() {
		if (!isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<AssignedToMeWidget> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}
}
