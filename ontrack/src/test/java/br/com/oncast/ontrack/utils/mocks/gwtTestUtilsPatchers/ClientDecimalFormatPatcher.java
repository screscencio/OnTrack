package br.com.oncast.ontrack.utils.mocks.gwtTestUtilsPatchers;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import java.text.DecimalFormat;

import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

@PatchClass(ClientDecimalFormat.class)
public class ClientDecimalFormatPatcher {

	@PatchMethod
	static String roundFloat(final float number, final int decimalDigits) {
		final DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(decimalDigits);

		return formatter.format(number);
	}
}
