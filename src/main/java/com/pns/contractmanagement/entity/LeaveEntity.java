package com.pns.contractmanagement.entity;

import java.util.List;

import com.pns.contractmanagement.model.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveEntity {
    /**
     *
     */

    private String employeeId;

    private List<LeaveQuotaEntity> leaveQuota;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveQuotaEntity {
        private LeaveType type;
        private float totalNo;
        private float reameningNo;
    }
}
