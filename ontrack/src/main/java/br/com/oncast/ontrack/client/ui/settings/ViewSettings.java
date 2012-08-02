package br.com.oncast.ontrack.client.ui.settings;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.HandlerRegistration;

public class ViewSettings {

	public enum ScopeTreeColumn {
		RELEASE() {
			@Override
			public boolean getDefaultValue() {
				return DefaultViewSettings.SCOPE_TREE_RELEASE_COLUMN_VISIBILITY;
			}
		},
		PROGRESS() {
			@Override
			public boolean getDefaultValue() {
				return DefaultViewSettings.SCOPE_TREE_PROGRESS_COLUMN_VISIBILITY;
			}
		},
		EFFORT() {
			@Override
			public boolean getDefaultValue() {
				return DefaultViewSettings.SCOPE_TREE_EFFORT_COLUMN_VISIBILITY;
			}
		},
		VALUE() {
			@Override
			public boolean getDefaultValue() {
				return DefaultViewSettings.SCOPE_TREE_VALUE_COLUMN_VISIBILITY;
			}
		};

		private boolean isVisibile;
		private final Set<VisibilityChangeListener> listeners;

		private ScopeTreeColumn() {
			this.isVisibile = getDefaultValue();
			listeners = new HashSet<VisibilityChangeListener>();
		}

		public boolean isVisible() {
			return isVisibile;
		}

		public boolean toggle() {
			isVisibile = !isVisibile;
			for (final VisibilityChangeListener l : listeners) {
				l.onVisiblityChange(isVisibile);
			}
			return isVisibile;
		}

		public HandlerRegistration register(final VisibilityChangeListener listener) {
			listeners.add(listener);
			listener.onVisiblityChange(isVisibile);

			return new HandlerRegistration() {
				@Override
				public void removeHandler() {
					listeners.remove(listener);
				}
			};
		}

		/**
		 * @deprecated Use {@link com.google.gwt.event.shared.HandlerRegistration HandlerRegistration#removeHandler()} returned by the
		 *             {@link #register(VisibilityChangeListener) register} method.
		 * @param listener
		 */
		@Deprecated
		public void unregister(final VisibilityChangeListener listener) {
			listeners.remove(listener);
		}

		public interface VisibilityChangeListener {

			void onVisiblityChange(boolean isVisible);

		}

		public void setVisibility(final boolean visibility) {
			if (this.isVisibile != visibility) this.toggle();
		}

		public abstract boolean getDefaultValue();
	}

}
