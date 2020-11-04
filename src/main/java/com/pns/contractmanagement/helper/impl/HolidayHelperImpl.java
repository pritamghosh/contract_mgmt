package com.pns.contractmanagement.helper.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Range;

@Component
public class HolidayHelperImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HolidayHelperImpl.class);

	private Map<String, Map<LocalDate, String>> holidayMap = new HashMap<>();

	private final String fileUrl;

	public HolidayHelperImpl(@Value("${app.holiday.file.url:holiday.xlsx}") String fileUrl) {
		this.fileUrl = fileUrl;
		loadHoliday();
	}

	public void loadHoliday() {
		try (FileInputStream file = new FileInputStream(new File(fileUrl))) {
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// Get first/desired sheet from the workbook
			final int numberOfSheets = workbook.getNumberOfSheets();

			for (int i = 0; i < numberOfSheets; i++) {
				final Sheet sheet = workbook.getSheetAt(i);
				final String location = sheet.getSheetName().toUpperCase();
				Map<LocalDate, String> map = holidayMap.get(location);
				if (map == null) {
					map = new HashMap<LocalDate, String>();
					holidayMap.put(location, map);
				}
				Iterator<Row> rowIterator = sheet.iterator();
				if (rowIterator.hasNext()) {
					rowIterator.next();
				}
				while (rowIterator.hasNext()) {
					final Row row = rowIterator.next();

					final Date dateCellValue = row.getCell(0).getDateCellValue();
					final String stringCellValue = row.getCell(1).getStringCellValue();
					map.put(dateCellValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), stringCellValue);
				}
			}

		} catch (IOException ex) {
			LOGGER.error("Exception occured", ex);
		}
	}

	public int countHolidays(Range<LocalDate> range, String region) {
		int count = 0;
		LocalDate date = range.lowerEndpoint();
		final Map<LocalDate, String> map = holidayMap.get(region.toUpperCase());
		while (!date.isAfter(range.upperEndpoint())) {
			DayOfWeek day = date.getDayOfWeek();
			if (map.containsKey(date) || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
				count++;
			}
			date = date.plusDays(1);
		}
		return count;

	}
}
