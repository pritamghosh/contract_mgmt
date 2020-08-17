package com.pns.contractmanagement.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.DecimalFormat;

public final class AmountUtil {

	private static final String EMPTY_SPACE = " ";
	private static final String HYPHEN = "-";
	private static Map<Integer, String> AMOUNT_WORD_MAP = new HashMap<>();
	static {
		AMOUNT_WORD_MAP.put(1, "ONE");
		AMOUNT_WORD_MAP.put(2, "TWO");
		AMOUNT_WORD_MAP.put(3, "THREE");
		AMOUNT_WORD_MAP.put(4, "FOUR");
		AMOUNT_WORD_MAP.put(5, "FIVE");
		AMOUNT_WORD_MAP.put(6, "SIX");
		AMOUNT_WORD_MAP.put(7, "SEVEN");
		AMOUNT_WORD_MAP.put(8, "EIGHT");
		AMOUNT_WORD_MAP.put(9, "NINE");
		AMOUNT_WORD_MAP.put(10, "TEN");
		AMOUNT_WORD_MAP.put(11, "ELEVEN");
		AMOUNT_WORD_MAP.put(12, "TWELVE");
		AMOUNT_WORD_MAP.put(13, "THIRTEEN");
		AMOUNT_WORD_MAP.put(14, "FOURTEEN");
		AMOUNT_WORD_MAP.put(15, "FIFTEEN");
		AMOUNT_WORD_MAP.put(16, "SIXTEEN");
		AMOUNT_WORD_MAP.put(17, "SEVENTEEN");
		AMOUNT_WORD_MAP.put(18, "EIGHTEEN");
		AMOUNT_WORD_MAP.put(19, "NINETEEN");
		AMOUNT_WORD_MAP.put(20, "TWENTY");
		AMOUNT_WORD_MAP.put(30, "THIRTY");
		AMOUNT_WORD_MAP.put(40, "FOURTY");
		AMOUNT_WORD_MAP.put(50, "FIFTY");
		AMOUNT_WORD_MAP.put(60, "SIXTY");
		AMOUNT_WORD_MAP.put(70, "SEVENTY");
		AMOUNT_WORD_MAP.put(80, "EIGHTY");
		AMOUNT_WORD_MAP.put(90, "NINETY");
	}

	public static String convertToWord(double amount) {
		DecimalFormat df = new DecimalFormat("#.##");
		final String str = df.format(amount);
		final String[] split = str.split("\\.");
		StringBuilder builder = new StringBuilder();
		convert(split[0], builder);
		if(split.length>1) {
			final String paise = convertTwoDigit(StringUtils.substring(split[1], 0,2),null);
			builder.append("AND ").append(paise).append("PAISE");
		}
		return builder.append(" ONLY").toString();
	}

	private static void convert(String strParam, StringBuilder builder) {
		String str = strParam;
		 int length = str.length();
		if (length > 7) {
			convert(str.substring(0, length - 7), builder);
			builder.append("CRORE ");
			str = str.substring(length - 7);
			length = str.length();
		}
		if (length > 5) {
			builder.append(convertTwoDigit(str.substring(0, length-5), "LAKH"));
			str = str.substring(length-5);
			length = str.length();
		}
		if (length > 3) {
			builder.append(convertTwoDigit(str.substring(0, length-3), "THOUSAND"));
			str = str.substring(length-3);
			length = str.length();
		}
		if (length > 2) {
			builder.append(convertTwoDigit(str.substring(0, 1), "HUNDRED"));
			str = str.substring(1);
		}
		builder.append(convertTwoDigit(str, null));
	}

	private static String convertTwoDigit(String str, String unitParam) {
		final int parseInt = Integer.parseInt(str);
		String unit = unitParam;
		if (parseInt == 0) {
			return "";
		}
		final StringBuilder ammoundBuilder = new StringBuilder();
		if (AMOUNT_WORD_MAP.get(parseInt) != null) {
			ammoundBuilder.append(AMOUNT_WORD_MAP.get(parseInt)).append(EMPTY_SPACE);
		} else {
			final int delemel = Integer.parseInt(String.valueOf(str.charAt(0)) + "0");
			final int unitPos = Integer.parseInt(String.valueOf(str.charAt(1)));
			ammoundBuilder.append(AMOUNT_WORD_MAP.get(delemel)).append(HYPHEN).append(AMOUNT_WORD_MAP.get(unitPos)).append(EMPTY_SPACE);

		}
		if (unit != null) {
			ammoundBuilder.append(unit).append(EMPTY_SPACE).toString();
		}
		return ammoundBuilder.toString();
	}
}
