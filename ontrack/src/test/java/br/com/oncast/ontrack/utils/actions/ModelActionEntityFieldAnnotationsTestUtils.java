package br.com.oncast.ontrack.utils.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import static org.junit.Assert.assertEquals;

public class ModelActionEntityFieldAnnotationsTestUtils {

	public static void assertField(final String actionName, final Field field) {
		for (final ModelActionEntityFieldAnnotationValidator handler : ModelActionEntityFieldAnnotationValidator.values()) {
			if (handler.handlesType(field)) {
				handler.validade(actionName, field);
				return;
			}
		}

		error("The field '" + field.getName() + "' with type " + field.getType().getSimpleName()
				+ " is unknown by the ModelActionEntityFieldAnnotationsTestUtils, please add a hander for this type or use a supported type");
	}

	private static void error(final String message) {
		throw new RuntimeException(message);
	}

	private enum ModelActionEntityFieldAnnotationValidator {

		DESCRIPTION_TEXT(String.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);

				if (column.length() != ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
					error("The field '" + field.getName() + "' should have ActionTableColumns.DESCRIPTION_TEXT_LENGTH as length value of @Column annotation");
			}

			@Override
			protected boolean accepts(final Field field) {
				final Column column = field.getAnnotation(Column.class);
				return column != null && ActionTableColumns.DESCRIPTION_TEXT.equals(column.name());
			}

		},

		UNIQUE_ID(String.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);

				ensureColumnName(column, "The field uniqueId should have ActionTableColumns.UNIQUE_ID as name attribute of annotation @Column", ActionTableColumns.UNIQUE_ID);
			}

			@Override
			protected boolean accepts(final Field field) {
				return field.getName().equals("uniqueId");
			}

		},

		STRING(String.class, Enum.class) {
			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);

				ensureColumnName(column, "The field " + field.getName() + " shoud have one of ActionTableColumns.STRING_[X] as name attribute of annotation @Column", ActionTableColumns.STRING_1,
						ActionTableColumns.STRING_2, ActionTableColumns.STRING_3, ActionTableColumns.STRING_4);
			}

			@Override
			protected boolean accepts(final Field field) {
				return !DESCRIPTION_TEXT.accepts(field);
			}
		},

		ENUM(Enum.class) {
			@Override
			public void validade(final String actionName, final Field field) {
				final Enumerated enumerated = getAnnotation(field, Enumerated.class);

				assertEquals("The field " + field.getName() + " should have EnumType.STRING as value of @Enumerated annotation", EnumType.STRING, enumerated.value());
				STRING.validade(actionName, field);
			}

			@Override
			protected boolean accepts(final Field field) {
				return true;
			}
		},

		BOOLEAN(boolean.class, Boolean.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have one of ActionTableColumns.BOOLEAN_[X] as name attribute of annotation @Column", ActionTableColumns.BOOLEAN_1,
						ActionTableColumns.BOOLEAN_2);

			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		INTEGER(int.class, Integer.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.INT_1 as name attribute of annotation @Column", ActionTableColumns.INT_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		FLOAT(float.class, Float.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.FLOAT_1 as name attribute of annotation @Column", ActionTableColumns.FLOAT_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		LONG(long.class, Long.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.LONG_1 as name attribute of annotation @Column", ActionTableColumns.LONG_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		DATE(Date.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.DATE_1 as name attribute of annotation @Column", ActionTableColumns.DATE_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		ACTION_LIST(List.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.ACTION_LIST as name attribute of annotation @Column", ActionTableColumns.ACTION_LIST);
				final OneToMany oneToMany = getAnnotation(field, OneToMany.class);
				if (!Arrays.asList(oneToMany.cascade()).contains(CascadeType.ALL)) error("The field " + field.getName() + " shoud have CascadeType.ALL as cascade attribute of annotation @OneToMany");
				final JoinTable joinTable = getAnnotation(field, JoinTable.class);
				if (!Pattern.matches("^" + actionName + "(Action)?_" + field.getName() + "$", joinTable.name()))
					error("The field " + field.getName() + " should have the name '" + actionName + "_" + field.getName() + "' or '" + actionName + "Action_" + field.getName() + "'");
			}

			@Override
			protected boolean accepts(final Field field) {
				return field.getName().toLowerCase().contains("subaction");
			}

		},

		MODEL_ACTION_ENTITY(ModelActionEntity.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				getAnnotation(field, OneToOne.class);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},
		STRING_LIST(List.class) {

			@Override
			public void validade(final String actionName, final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column, "The field " + field.getName() + " shoud have ActionTableColumns.STRING_LIST_[X] as name attribute of annotation @Column", ActionTableColumns.STRING_LIST_1,
						ActionTableColumns.STRING_LIST_2);
				getAnnotation(field, ElementCollection.class);
			}

			@Override
			protected boolean accepts(final Field field) {
				return true;
			}

		};

		private final Class<?>[] handledTypes;

		protected boolean ACCEPTS_ANY_FIELD = true;

		private ModelActionEntityFieldAnnotationValidator(final Class<?>... handledTypes) {
			this.handledTypes = handledTypes;
		}

		public boolean handlesType(final Field field) {
			final Class<?> fieldType = field.getType();
			for (final Class<?> handledType : handledTypes) {
				if (handledType.isAssignableFrom(fieldType)) return accepts(field);
			}

			return false;
		}

		protected <T extends Annotation> T getAnnotation(final Field field, final Class<T> annotationType) {
			final T annotation = field.getAnnotation(annotationType);
			if (annotation == null) error("The field " + field.getName() + " should have @" + annotationType.getSimpleName() + " annotation");
			return annotation;
		}

		protected void ensureColumnName(final Column column, final String errorMessage, final String... expectedNames) {
			if (!Arrays.asList(expectedNames).contains(column.name())) error(errorMessage);
		}

		public abstract void validade(String actionName, final Field field);

		protected abstract boolean accepts(Field field);

	}

}
