package br.com.oncast.ontrack.client.utils.number;

public class ClientDecimalFormat {

	// TODO Review this number formating method;
	public static native String roundFloat(final float number, int decimalDigits) /*-{
		var num = new Number(number);
		return num.toFixed(decimalDigits).toString();
	}-*/;

	public static String removeUnnecessaryRightZeros(final String numberString) {
		return numberString.replaceAll("\\.0+$", "");
	}

	public static String roundAndRemoveUnnecessaryRightZeros(final float number, final int decimalDigits) {
		return removeUnnecessaryRightZeros(roundFloat(number, decimalDigits));
	}
}