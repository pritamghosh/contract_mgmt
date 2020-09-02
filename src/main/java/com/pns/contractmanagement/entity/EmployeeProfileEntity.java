package com.pns.contractmanagement.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeProfileEntity extends BaseMongoEntity {

	private String employeeId;
	private String firstName;
	private String familyName;
	private String middleName;
	private String address;
	private String designation;
	private String baseLocation;
	private LocalDate dateOfBirth;
	private LocalDate dateOfJoining;
	private String workEmail;
	private String mobileNo;
	private String gender;
	private String workContactNo;
	private String bloodGroup;
	private byte[] image;
	@Builder.Default
	private Status status = Status.ACTIVE;

	private ManagerEntity reportingManager;
	private ManagerEntity hrManager;

	public enum Status {
		ACTIVE, INACTIVE;
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = false)
	public static class ManagerEntity {
		@JsonProperty("_id")
		private String id;
		private String name;
		private String employeeId;
	}

}
