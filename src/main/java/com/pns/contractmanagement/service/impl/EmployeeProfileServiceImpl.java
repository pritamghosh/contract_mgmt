package com.pns.contractmanagement.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.Status;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.UserRegisterHelperImpl;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.ImmutableEmployeeProfile;
import com.pns.contractmanagement.service.EmployeeProfileService;
import com.pns.contractmanagement.util.ServiceUtil;

/**
 * Service Implementation of {@link EmployeeProfileService}.
 */
@Service
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

	@Value("${employee.id.length:7}")
	private int employeeIdLength;
	@Autowired
	private EmployeeProfileDao employeeProfileDao;

	@Autowired
	private UserRegisterHelperImpl userRegisterHelper;

	/** {@inheritDoc} */
	@Override
	public EmployeeProfile createEmployeeProfile(final EmployeeProfile employeeProfile) {
		if (employeeProfileDao.findByEmail(employeeProfile.getWorkEmail()).isPresent()) {
			throw new PnsException("Profile Exists with same Email Id");
		}
		if(!userRegisterHelper.getListOfGroups().contains(employeeProfile.getDesignation())) {
			throw new PnsException("Desgnation is not Available");
		}
		final EmployeeProfileEntity profileToInsert = map(employeeProfile);
		final SequenceEntity employeeSequence = employeeProfileDao.findAndUpdateSequece();
		final String sequence = String.valueOf(employeeSequence.getSequence());
		StringBuilder idBuilder = new StringBuilder("P");
		while (idBuilder.length() + sequence.length() < employeeIdLength) {
			idBuilder.append(0);
		}
		idBuilder.append(sequence);
		profileToInsert.setEmployeeId(idBuilder.toString());
		final EmployeeProfile newProfile = map(employeeProfileDao.insert(profileToInsert));
		try {
			userRegisterHelper.registerUser(newProfile);
		} catch (PnsException ex) {
			throw new PnsException("Unable to Register Employee for Login", ex);
		}
		return newProfile;
	}

	/** {@inheritDoc} */
	@Override
	public EmployeeProfile getEmployeeProfile() {
		final String username = ServiceUtil.getUsernameFromContext();
		final Optional<EmployeeProfileEntity> employeeProfileByEmployeeId = employeeProfileDao
				.findByEmployeeId(username);
		if (employeeProfileByEmployeeId.isPresent() && Status.ACTIVE == employeeProfileByEmployeeId.get().getStatus()) {
			return map(employeeProfileByEmployeeId.get());
		}
		throw new PnsException("No Active User Found");
	}
	
	@Override
	public EmployeeProfile uploadImage(byte[] image) {
		final String employeeId = ServiceUtil.getUsernameFromContext();
		if(employeeProfileDao.saveImage(employeeId,image)) {
			return getEmployeeProfile();
		};
		throw new PnsException("Unable to save Profile Picture");
	}

	EmployeeProfileEntity map(final EmployeeProfile profile) {
		// @formatter:off
		final EmployeeProfileEntity entity = EmployeeProfileEntity.builder().address(profile.getAddress())
				.baseLocation(profile.getBaseLocation()).bloodGroup(profile.getBloodGroup())
				.dateOfBirth(profile.getDateOfBirth()).dateOfJoining(profile.getDateOfJoining())
				.designation(profile.getDesignation()).employeeId(profile.getEmployeeId())
				.familyName(profile.getFamilyName()).firstName(profile.getFirstName()).gender(profile.getGender())
				.image(profile.getImage()).middleName(profile.getMiddleName()).mobileNo(profile.getMobileNo())
				.workContactNo(profile.getWorkContactNo()).workEmail(profile.getWorkEmail()).build();
		// @formatter:on
		entity.setId(profile.getId());
		return entity;

	}

	EmployeeProfile map(final EmployeeProfileEntity entity) {
		// @formatter:off
		final EmployeeProfile profile = ImmutableEmployeeProfile.builder().address(entity.getAddress())
				.baseLocation(entity.getBaseLocation()).bloodGroup(entity.getBloodGroup())
				.dateOfBirth(entity.getDateOfBirth()).dateOfJoining(entity.getDateOfJoining())
				.designation(entity.getDesignation()).employeeId(entity.getEmployeeId())
				.familyName(entity.getFamilyName()).firstName(entity.getFirstName()).gender(entity.getGender())
				.image(entity.getImage()).middleName(entity.getMiddleName()).mobileNo(entity.getMobileNo())
				.workContactNo(entity.getWorkContactNo()).workEmail(entity.getWorkEmail()).id(entity.getId()).build();
		// @formatter:on
		return profile;

	}

	
}
