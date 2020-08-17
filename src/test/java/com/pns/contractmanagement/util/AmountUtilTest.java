package com.pns.contractmanagement.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
		System.out.println( AmountUtil.convertToWord(203234d));
	}

}
