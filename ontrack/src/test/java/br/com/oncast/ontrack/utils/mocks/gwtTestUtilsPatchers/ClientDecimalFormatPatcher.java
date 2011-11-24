package br.com.oncast.ontrack.utils.mocks.gwtTestUtilsPatchers;

import java.text.DecimalFormat;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;

import com.octo.gwt.test.patchers.PatchClass;
import com.octo.gwt.test.patchers.PatchMethod;

@PatchClass(ClientDecimalFormat.class)
public class ClientDecimalFormatPatcher {

	@PatchMethod
	static String roundFloat(final float number, final int decimalDigits) {
		final DecimalFormat formatter = new DecimalFormat();
		formatter.setMaximumFractionDigits(decimalDigits);

		return formatter.format(number);
	}
}
