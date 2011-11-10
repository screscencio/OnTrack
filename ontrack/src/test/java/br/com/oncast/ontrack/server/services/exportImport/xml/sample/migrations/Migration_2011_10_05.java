package br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2011_10_05 extends Migration {

	@Override
	public void execute() {
		getRootElement().addElement(this.getClass().getSimpleName());
	}

}
