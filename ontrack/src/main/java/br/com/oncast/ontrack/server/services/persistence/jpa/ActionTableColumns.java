package br.com.oncast.ontrack.server.services.persistence.jpa;

/**
 * Class to centralize column names, this way avoiding unnecessary creation or misspelling of names.
 */
public interface ActionTableColumns {

	public static final String ACTION_LIST = "modelActionEntity_subActionList";

	public final static String DESCRIPTION_TEXT = "description";
	public static final int DESCRIPTION_TEXT_LENGTH = 400;

	public final static String FLOAT_1 = "float_1";

	public final static String STRING_1 = "referenceId";
	public static final String STRING_2 = "secondaryReferenceId";
	public static final String STRING_3 = "secundaryReferenceId";
	public static final String STRING_4 = "string_4";

	public static final String BOOLEAN_1 = "boleano";

	public static final String INT_1 = "pos";

	public static final String DATE_1 = "timestamp";

	public static final String LONG_1 = "long_1";

	public static final String STRING_LIST_1 = "modelActionEntity_stringList_1";
	public static final String STRING_LIST_2 = "modelActionEntity_stringList_2";

}
