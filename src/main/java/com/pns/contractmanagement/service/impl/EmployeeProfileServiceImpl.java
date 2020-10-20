package com.pns.contractmanagement.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.ManagerEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.Status;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.UserRegisterHelperImpl;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.ImmutableEmployeeProfile;
import com.pns.contractmanagement.model.ImmutableManager;
import com.pns.contractmanagement.model.Manager;
import com.pns.contractmanagement.service.EmployeeProfileService;
import com.pns.contractmanagement.util.ServiceUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation of {@link EmployeeProfileService}.
 */
@Service
@Slf4j
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
		if (!userRegisterHelper.getListOfGroups().contains(employeeProfile.getDesignation())) {
			throw new PnsException("Desgnation is not Available");
		}
		final EmployeeProfileEntity profileToInsert = map(employeeProfile);
		final SequenceEntity employeeSequence = employeeProfileDao.findAndUpdateSequece();
		final String sequence = String.valueOf(employeeSequence.getSequence());
		StringBuilder idBuilder = new StringBuilder("p");
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
		final EmployeeProfile findProfileById = findProfileById(username);
		if (findProfileById != null) {
			return findProfileById;
		}
		log.error("No Active User Found for {} ", username);
		throw new PnsException("No Active User Found");
	}

	@Override
	public EmployeeProfile uploadImage(byte[] image) throws IOException {
		final String employeeId = ServiceUtil.getUsernameFromContext();
		if (employeeProfileDao.saveImage(employeeId, processImage(image))) {
			return getEmployeeProfile();
		}
		;
		throw new PnsException("Unable to save Profile Picture");
	}

	private byte[] processImage(byte[] image) throws IOException {
		if(image.length<=500000) {
			return image;
		}
		final float quality = 500000f/image.length;
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(image));
		ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/jpeg").next();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		try(out;ios;) {

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(quality);
			}

			writer.write(null, new IIOImage(bi, null, null), param);
			byte[] imageByte = out.toByteArray();
			return imageByte;
		} finally {
			writer.dispose();
		}
	}

	/** {@inheritDoc} */
	@Override
	public EmployeeProfile findProfileById(String id) {
		final Optional<EmployeeProfileEntity> employeeProfileByEmployeeId = employeeProfileDao.findByEmployeeId(id);
		if (employeeProfileByEmployeeId.isPresent() && Status.ACTIVE == employeeProfileByEmployeeId.get().getStatus()) {
			return map(employeeProfileByEmployeeId.get(), true);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<Manager> searchManager(String query) {
		return employeeProfileDao.searchByQuery(query).stream().map(this::mapManger).collect(Collectors.toList());
	}

	private EmployeeProfileEntity map(final EmployeeProfile profile) {
		// @formatter:off
		final EmployeeProfileEntity entity = EmployeeProfileEntity.builder().address(profile.getAddress())
				.baseLocation(profile.getBaseLocation()).bloodGroup(profile.getBloodGroup())
				.dateOfBirth(profile.getDateOfBirth()).dateOfJoining(profile.getDateOfJoining())
				.designation(profile.getDesignation()).employeeId(profile.getEmployeeId())
				.familyName(profile.getFamilyName()).firstName(profile.getFirstName()).gender(profile.getGender())
				.image(profile.getImage()).middleName(profile.getMiddleName()).mobileNo(profile.getMobileNo())
				.reportingManager(map(profile.getReportingManager())).hrManager(map(profile.getHrManager()))
				.workContactNo(profile.getWorkContactNo()).workEmail(profile.getWorkEmail()).build();
		// @formatter:on
		entity.setId(profile.getId());
		return entity;

	}

	private EmployeeProfile map(final EmployeeProfileEntity entity) {
		return map(entity, false);

	}

	private EmployeeProfile map(final EmployeeProfileEntity entity, boolean includeImage) {
		// @formatter:off
		return ImmutableEmployeeProfile.builder().address(entity.getAddress()).baseLocation(entity.getBaseLocation())
				.bloodGroup(entity.getBloodGroup()).dateOfBirth(entity.getDateOfBirth())
				.dateOfJoining(entity.getDateOfJoining()).designation(entity.getDesignation())
				.employeeId(entity.getEmployeeId()).familyName(entity.getFamilyName()).firstName(entity.getFirstName())
				.gender(entity.getGender()).image(includeImage ? entity.getImage() : null)
				.middleName(entity.getMiddleName()).mobileNo(entity.getMobileNo())
				.workContactNo(entity.getWorkContactNo()).workEmail(entity.getWorkEmail()).id(entity.getId())
				.reportingManager(map(entity.getReportingManager())).hrManager(map(entity.getHrManager())).build();
		// @formatter:on

	}

	private Manager map(final ManagerEntity entity) {
		return entity == null ? null
				: ImmutableManager.builder().name(entity.getName()).id(entity.getId())
						.employeeId(entity.getEmployeeId()).build();
	}

	private ManagerEntity map(final Manager entity) {
		return entity == null ? null
				: ManagerEntity.builder().name(entity.getName()).id(entity.getId()).employeeId(entity.getEmployeeId())
						.build();
	}

	private Manager mapManger(final EmployeeProfileEntity entity) {
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(entity.getFirstName()).append(" ");
		if (StringUtils.isNotBlank(entity.getMiddleName())) {
			nameBuilder.append(entity.getMiddleName()).append(" ");
		}
		nameBuilder.append(entity.getFamilyName()).append("(").append(entity.getEmployeeId()).append(")");
		return ImmutableManager.builder().name(nameBuilder.toString()).id(entity.getId())
				.employeeId(entity.getEmployeeId()).build();

	}

}
