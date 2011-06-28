package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReflectionTest {
	private final List<String> list = new ArrayList<String>();

	@Test
	public void reflectionTest() throws SecurityException, NoSuchFieldException {
		final Field field = this.getClass().getDeclaredField("list");
		field.setAccessible(true);
		System.out.println(field.getType());
		final Type type = field.getGenericType();
		System.out.println(type);

		if (type instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) type;
			System.out.println("raw type: " + pt.getRawType());
			System.out.println("owner type: " + pt.getOwnerType());
			System.out.println("actual type args:");
			for (final Type t : pt.getActualTypeArguments()) {
				System.out.println("    " + t);
			}

		}
	}
}
