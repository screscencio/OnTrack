package br.com.oncast.ontrack.client.ui.components.report;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ReportMessages extends BaseMessages {

	@Description("message shown in the report table to indicate that an impediment is solved")
	@DefaultMessage("Solved")
	String solvedImpediment();

	@Description("message shown in the report table to indicate that an impediment is open")
	@DefaultMessage("Open")
	String openImpediment();

	@Description("message shown in the report table to indicate that an impediment is deprecated")
	@DefaultMessage("Deprecated")
	String deprecatedImpediment();

	@Description("message shown in the report table as header for impediment status column")
	@DefaultMessage("Status")
	String status();

	@Description("message shown in the report table as header for impediment description column")
	@DefaultMessage("Description")
	String impedimentDescription();

	@Description("message shown in the report table as header for scope id column")
	@DefaultMessage("ID")
	String id();

	@Description("message shown in the report table as header for impediment creation date column")
	@DefaultMessage("Start")
	String startDate();

	@Description("message shown in the report table as header for impediment solution date column")
	@DefaultMessage("End")
	String endDate();

	@Description("message shown in the report table as header for cycle time column")
	@DefaultMessage("Cycle time")
	String cycleTime();

	@Description("message shown in the report table as header for scope description column")
	@DefaultMessage("Description")
	String scopeDescription();

	@Description("message shown in the report table as header for scope effort column")
	@DefaultMessage("Effort")
	String effort();

	@Description("message shown in the report table as header for scope value column")
	@DefaultMessage("Value")
	String value();

	@Description("message shown in the report table as header for scope progress column")
	@DefaultMessage("Progress")
	String progress();

	@Description("message shown in the report table as header for lead time column")
	@DefaultMessage("Lead Time")
	String leadTime();

	@Description("message shown in the report table indicating that the impediment belongs to the release")
	@DefaultMessage("Release")
	String release();

	@Description("essage shown in the impediments table as header for related to column")
	@DefaultMessage("Related")
	String related();

}