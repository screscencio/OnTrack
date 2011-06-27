package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

@Retention(RetentionPolicy.RUNTIME)
public @interface PersistentEntityFor {
	Class<? extends ModelAction> value();
}
