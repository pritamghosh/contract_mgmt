package com.pns.contractmanagement.model;

import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableHolidayCalendar.Builder.class)
public interface HolidayCalendar {
 String getRegion();
 
 List<Holiday> getDetails();
 
 @Value.Immutable
 @JsonDeserialize(builder = ImmutableHoliday.Builder.class)
 public interface Holiday {
	 LocalDate getDate();
	 
	 String getOccasion();
	 
	 String getDay();
	 
 }
}
