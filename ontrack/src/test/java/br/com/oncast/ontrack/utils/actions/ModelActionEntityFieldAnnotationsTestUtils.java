package br.com.oncast.ontrack.utils.actions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;

public class ModelActionEntityFieldAnnotationsTestUtils {

	public static void assertField(final Field field) {
		for (final ModelActionEntityFieldAnnotationValidator handler : ModelActionEntityFieldAnnotationValidator.values()) {
			if (handler.handlesType(field)) {
				handler.validade(field);
				return;
			}
		}

		error("The type " + field.getType().getSimpleName()
				+ " is unknown by the ModelActionEntityFieldAnnotationsTestUtils, please add a hander for this type or use another type");
	}

	private static void error(final String message) {
		throw new RuntimeException(message);
	}

	private enum ModelActionEntityFieldAnnotationValidator {

		DESCRIPTION_TEXT(String.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);

				if (column.length() != ActionTableColumns.DESCRIPTION_TEXT_LENGTH) error("The field '" + field.getName()
						+ "' should have ActionTableColumns.DESCRIPTION_TEXT_LENGTH as length value of @Column annotation");
			}

			@Override
			protected boolean accepts(final Field field) {
				final Column column = field.getAnnotation(Column.class);
				return column != null && ActionTableColumns.DESCRIPTION_TEXT.equals(column.name());
			}

		},

		STRING(String.class) {
			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);

				ensureColumnName(column,
						"The field " + field.getName() + " shoud have one of ActionTableColumns.STRING_[X] as name attribute of annotation @Column",
						ActionTableColumns.STRING_1,
						ActionTableColumns.STRING_2,
						ActionTableColumns.STRING_3);
			}

			@Override
			protected boolean accepts(final Field field) {
				return !DESCRIPTION_TEXT.accepts(field);
			}
		},

		BOOLEAN(boolean.class, Boolean.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.BOOLEAN_1 as name attribute of annotation @Column",
						ActionTableColumns.BOOLEAN_1);

			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		INTEGER(int.class, Integer.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.INT_1 as name attribute of annotation @Column",
						ActionTableColumns.INT_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		FLOAT(float.class, Float.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.FLOAT_1 as name attribute of annotation @Column",
						ActionTableColumns.FLOAT_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		LONG(long.class, Long.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.LONG_1 as name attribute of annotation @Column",
						ActionTableColumns.LONG_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		DATE(Date.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.DATE_1 as name attribute of annotation @Column",
						ActionTableColumns.DATE_1);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		},

		ACTION_LIST(List.class) {

			@Override
			public void validade(final Field field) {
				final Column column = getAnnotation(field, Column.class);
				ensureColumnName(column,
						"The field " + field.getName() + " shoud have ActionTableColumns.ACTION_LIST as name attribute of annotation @Column",
						ActionTableColumns.ACTION_LIST);
				final OneToMany oneToMany = getAnnotation(field, OneToMany.class);
				if (!Arrays.asList(oneToMany.cascade()).contains(CascadeType.ALL)) error("The field " + field.getName()
						+ " shoud have CascadeType.ALL as cascade attribute of annotation @OneToMany");
			}

			@Override
			protected boolean accepts(final Field field) {
				return field.getName().toLowerCase().contains("subaction");
			}

		},

		MODEL_ACTION_ENTITY(ModelActionEntity.class) {

			@Override
			public void validade(final Field field) {
				getAnnotation(field, OneToOne.class);
			}

			@Override
			protected boolean accepts(final Field field) {
				return ACCEPTS_ANY_FIELD;
			}

		};

		private final Class<?>[] handledTypes;

		protected boolean ACCEPTS_ANY_FIELD = true;

		private ModelActionEntityFieldAnnotationValidator(final Class<?>... handledTypes) {
			this.handledTypes = handledTypes;
		}

		public boolean handlesType(final Field field) {
			for (final Class<?> type : handledTypes) {
				if (field.getType().isAssignableFrom(type)) return accepts(field);
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

		public abstract void validade(final Field field);

		protected abstract boolean accepts(Field field);

	}

}
