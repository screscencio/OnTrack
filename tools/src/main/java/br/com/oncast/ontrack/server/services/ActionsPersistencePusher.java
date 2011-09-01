package br.com.oncast.ontrack.server.services;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class ActionsPersistencePusher {
	private final List<ModelAction> actions;
	private final GregorianCalendar calendar = new GregorianCalendar();

	private ActionsPersistencePusher(final List<ModelAction> actions) {
		this.actions = actions;
	}

	public static void push(final List<ModelAction> actions) {
		try {
			new ActionsPersistencePusher(actions).push();
		}
		catch (final PersistenceException e) {
			throw new RuntimeException("Unable to persist action.",e);
		}
	}

	private void push() throws PersistenceException {
		prepareDateGenerator();

		final PersistenceService p = new PersistenceServiceJpaImpl();
		for (final ModelAction ma : actions) {
			p.persistActions(Collections.singletonList(ma), nextDate());
		}
	}

	private void prepareDateGenerator() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, -actions.size());
	}

	private Date nextDate() {
		final Date result = calendar.getTime();
		calendar.add(Calendar.SECOND, 1);
		return result;
	}
}
