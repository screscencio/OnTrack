package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.client.ui.places.metrics.OnTrackMetricsPlace;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
<<<<<<< HEAD
import br.com.oncast.ontrack.client.ui.places.timesheet.TimesheetPlace;
=======
import br.com.oncast.ontrack.client.ui.places.report.ReportPlace;
>>>>>>> work

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ PlanningPlace.Tokenizer.class, ProjectSelectionPlace.Tokenizer.class, ProgressPlace.Tokenizer.class, DetailPlace.Tokenizer.class,
		OrganizationPlace.Tokenizer.class, OnTrackMetricsPlace.Tokenizer.class, ReportPlace.Tokenizer.class,  TimesheetPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {}
