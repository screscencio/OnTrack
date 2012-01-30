package br.com.oncast.ontrack.client.ui.components.progresspanel;

import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.release.Release;

public interface KanbanWigetDisplay {

	public void configureKanbanPanel(final Kanban kanban, final Release release);

	public void update();
}