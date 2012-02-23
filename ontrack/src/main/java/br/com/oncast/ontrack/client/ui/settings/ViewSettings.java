package br.com.oncast.ontrack.client.ui.settings;

import java.util.HashSet;
import java.util.Set;

public class ViewSettings {

	public enum VisibilityOf {
		RELEASE(DefaultViewSettings.SCOPE_TREE_RELEASE_COLUMN_VISIBILITY),
		PROGRESS(DefaultViewSettings.SCOPE_TREE_PROGRESS_COLUMN_VISIBILITY),
		EFFORT(DefaultViewSettings.SCOPE_TREE_EFFORT_COLUMN_VISIBILITY),
		VALUE(DefaultViewSettings.SCOPE_TREE_VALUE_COLUMN_VISIBILITY);

		private boolean isVisibile;
		private final Set<VisibilityChangeListener> listeners;

		private VisibilityOf(final boolean visibility) {
			this.isVisibile = visibility;
			listeners = new HashSet<VisibilityChangeListener>();
		}

		public boolean toggle() {
			isVisibile = !isVisibile;
			for (final VisibilityChangeListener l : listeners) {
				l.onVisiblityChange(isVisibile);
			}
			return isVisibile;
		}

		public void register(final VisibilityChangeListener listener) {
			listeners.add(listener);
			listener.onVisiblityChange(isVisibile);
		}

		public void unregister(final VisibilityChangeListener listener) {
			listeners.remove(listener);
		}

		public interface VisibilityChangeListener {

			void onVisiblityChange(boolean isVisible);

		}
	}

}
