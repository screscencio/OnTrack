package br.com.oncast.ontrack.patchers;

import java.text.DecimalFormat;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.octo.gwt.test.patchers.AutomaticPatcher;
import com.octo.gwt.test.patchers.PatchClass;
import com.octo.gwt.test.patchers.PatchMethod;

@PatchClass(ClientDecimalFormat.class)
public class ClientDecimalFormatPatcher extends AutomaticPatcher {

	@PatchMethod
	public static String roundFloat(final float number, final int decimalDigits) {
		final DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(decimalDigits);

		return formatter.format(number);
	}
}
