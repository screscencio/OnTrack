package br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a class that wants to be translated and persisted. You have to define a target class for which the annotated class will be translated.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MapTo {
	Class<?> value();
}
