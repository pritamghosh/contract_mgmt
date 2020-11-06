package com.pns.contractmanagement.model;

/**
 *
 */
public enum LeaveType {
CL("Casual Leave"),PL("Privilleged Leave"),SL("Sick Leave");
	
	private final String value;

	private LeaveType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	
	
	
}
