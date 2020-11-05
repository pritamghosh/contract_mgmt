package com.pns.contractmanagement.entity;

import java.time.LocalDate;
import java.util.List;

import com.pns.contractmanagement.model.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class LeaveDetailsEntity   extends AbstractMongoEntity{
    /**
     *
     */

    private String employeeId;
    
    private long year;

    private List<LeaveQuotaEntity> leaveQuota;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveQuotaEntity {
    	private  LeaveType type;
    	  private  float totalLeaves;
    	  private  float reameningLeaves;
    	  private  float usedLeaves;
    	  private  float approvalPendingLeaves;
    	  private  LocalDate deductableFrom;
    	  private  LocalDate deductableTo;
    }
}
