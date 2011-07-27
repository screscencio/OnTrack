package br.com.oncast.ontrack.server.utils.typeConverter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.oncast.ontrack.server.utils.typeConverter.TypeConverter;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertUsing {
	Class<? extends TypeConverter> value();
}
