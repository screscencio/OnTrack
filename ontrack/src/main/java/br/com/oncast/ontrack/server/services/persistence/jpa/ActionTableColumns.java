package br.com.oncast.ontrack.server.services.persistence.jpa;

/**
 * Class to centralize column names, this way avoiding unnecessary creation or misspelling of names.
 */
public interface ActionTableColumns {

	public static final String ACTION_LIST = "modelActionEntity_subActionList";
	public final static String FLOAT = "float_1";
	public final static String STRING_1 = "referenceId";
	public final static String STRING_2 = "description";
	public static final String STRING_3 = "secondaryReferenceId";
	public static final String STRING_4 = "secundaryReferenceId";
	public static final String BOOLEAN = "boleano";
	public static final String INTEGER = "pos";
	public static final String TIMESTAMP = "timestamp";

}
