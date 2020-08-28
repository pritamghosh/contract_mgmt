package com.pns.contractmanagement.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.Status;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.ImmutableEmployeeProfile;
import com.pns.contractmanagement.service.EmployeeProfileService;
import com.pns.contractmanagement.util.ServiceUtil;

/**
 * Service Implementaion of {@link EmployeeProfileService}.
 */
@Service
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    @Autowired
    private EmployeeProfileDao employeeProfileDao;

    /** {@inheritDoc} */
    @Override
    public EmployeeProfile createEmployeeProfile(final EmployeeProfile employeeProfile) {
        return map(employeeProfileDao.insert(map(employeeProfile)));
    }

    /** {@inheritDoc} */
    @Override
    public EmployeeProfile getEmployeeProfileEmployeeId() {
        final String username = ServiceUtil.getUsernameFromContext();
        final Optional<EmployeeProfileEntity> employeeProfileByEmployeeId = employeeProfileDao
            .getEmployeeProfileByEmployeeId(username);
        if (employeeProfileByEmployeeId.isPresent() && Status.ACTIVE == employeeProfileByEmployeeId.get().getStatus()) {
            return map(employeeProfileByEmployeeId.get());
        }
        throw new PnsException("No Active User Found");
    }

    EmployeeProfileEntity map(final EmployeeProfile profile) {
        // @formatter:off
        final EmployeeProfileEntity entity = EmployeeProfileEntity.builder()
        .address(profile.getAddress())
        .baseLocation(profile.getBaseLocation())
        .bloodGroup(profile.getBloodGroup())
        .dateOfBirth(profile.getDateOfBirth())
        .dateOfJoining(profile.getDateOfJoining())
        .designation(profile.getDesignation())
        .employeeId(profile.getEmployeeId())
        .familyName(profile.getFamilyName())
        .firstName(profile.getFirstName())
        .gender(profile.getGender())
        .image(profile.getImage())
        .middleName(profile.getMiddleName())
        .mobileNo(profile.getMobileNo())
        .workContactNo(profile.getWorkContactNo())
        .workEmail(profile.getWorkEmail())
        .build();
        // @formatter:on
        entity.setId(profile.getId());
        return entity;

    }

    EmployeeProfile map(final EmployeeProfileEntity entity) {
        // @formatter:off
        final EmployeeProfile profile = ImmutableEmployeeProfile.builder()
        .address(entity.getAddress())
        .baseLocation(entity.getBaseLocation())
        .bloodGroup(entity.getBloodGroup())
        .dateOfBirth(entity.getDateOfBirth())
        .dateOfJoining(entity.getDateOfJoining())
        .designation(entity.getDesignation())
        .employeeId(entity.getEmployeeId())
        .familyName(entity.getFamilyName())
        .firstName(entity.getFirstName())
        .gender(entity.getGender())
        .image(entity.getImage())
        .middleName(entity.getMiddleName())
        .mobileNo(entity.getMobileNo())
        .workContactNo(entity.getWorkContactNo())
        .workEmail(entity.getWorkEmail())
        .id(entity.getId())
        .build();
        // @formatter:on
        return profile;

    }
}
