package com.pns.contractmanagement.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class AmountUtilTest {

	@Test
	void test() throws IOException {
//		BufferedWriter writer = new BufferedWriter(new FileWriter("sample.csv"));
//		for (double i = 1d; i < 203234d; i++) {
//			writer.write("" + i + "," + AmountUtil.convertToWord(i)+"\n");
//			System.out.println("" + i + "," + AmountUtil.convertToWord(i));
//		}
//		writer.close();
		assertEquals("TWO LAKH THREE THOUSAND TWO HUNDRED THIRTY-FOUR ONLY",
				AmountUtil.convertToWord(203234));
		assertEquals("SIXTEEN LAKH THIRTY-SIX THOUSAND NINE HUNDRED FOURTEEN AND SIXTY-NINE PAISE ONLY",
				AmountUtil.convertToWord(1636914.69));
	}

}
