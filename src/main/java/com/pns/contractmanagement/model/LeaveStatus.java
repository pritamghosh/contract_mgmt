package com.pns.contractmanagement.model;

/**
 *
 */
public enum LeaveStatus {
	PENDING("Pending"), APPROVED("Approved"), REJECTED("Rejected"), CANCELLED("Cancelled"),
	CANCEL_PENDING("Cancel Pending"), CANCEL_REJECTED("Cancel Rejected");
	private String action;

	public String getAction() {
		return action;
	}

	private LeaveStatus(String action) {
		this.action = action;
	}

}
