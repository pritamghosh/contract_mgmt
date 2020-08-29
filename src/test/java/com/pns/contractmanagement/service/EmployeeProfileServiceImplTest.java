package com.pns.contractmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.Status;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.UserRegisterHelperImpl;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.ImmutableEmployeeProfile;
import com.pns.contractmanagement.service.impl.EmployeeProfileServiceImpl;
import com.pns.contractmanagement.util.ServiceUtil;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class EmployeeProfileServiceImplTest {

	/** LOCAL_DATE. */

	@Mock
	private EmployeeProfileDao employeeProfileDao;

	@InjectMocks
	private EmployeeProfileServiceImpl service;

	@Captor
	ArgumentCaptor<EmployeeProfileEntity> entityCaptor;

	@Captor
	ArgumentCaptor<EmployeeProfile> profileCaptor;

	@Mock
	private UserRegisterHelperImpl userRegisterHelper;

	@Captor
	ArgumentCaptor<String> stringCaptor;

	MockedStatic<ServiceUtil> mockedServiceUtil;

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(service, "employeeIdLength", 7);
		mockedServiceUtil = Mockito.mockStatic(ServiceUtil.class);
		mockedServiceUtil.when(ServiceUtil::getUsernameFromContext).thenReturn("employeMockUserId");

	}

	@AfterEach
	void dispose() {
		mockedServiceUtil.close();

	}

	@Test
	void getEmployeeProfileEmployeeIdTest() {
		when(employeeProfileDao.findByEmployeeId(Mockito.anyString()))
				.thenReturn(Optional.of(mockEmployeeProfileEntity()));
		final EmployeeProfile employeeProfile = service.getEmployeeProfileEmployeeId();
		verify(employeeProfileDao, only()).findByEmployeeId(stringCaptor.capture());
		assertEquals(mockEmployeeProfile(), employeeProfile);
		assertEquals("employeMockUserId", stringCaptor.getValue());
	}

	@Test
	void getEmployeeProfileEmployeeIdNotActiveTest() {
		final EmployeeProfileEntity mockEmployeeProfileEntity = mockEmployeeProfileEntity();
		mockEmployeeProfileEntity.setStatus(Status.INACTIVE);
		when(employeeProfileDao.findByEmployeeId(Mockito.anyString()))
				.thenReturn(Optional.of(mockEmployeeProfileEntity));
		assertThrows(PnsException.class, () -> service.getEmployeeProfileEmployeeId());
		verify(employeeProfileDao, only()).findByEmployeeId(stringCaptor.capture());
		assertEquals("employeMockUserId", stringCaptor.getValue());
	}

	@Test
	void insertTest() {
		final EmployeeProfileEntity mockEmployeeProfileEntity = mockEmployeeProfileEntity();
		when(userRegisterHelper.getListOfGroups()).thenReturn(List.of("designation"));
		when(employeeProfileDao.insert(Mockito.any())).thenReturn(mockEmployeeProfileEntity);
		when(employeeProfileDao.findAndUpdateSequece()).thenReturn(SequenceEntity.builder().sequence(134).build());
		final EmployeeProfile employeeProfile = service.createEmployeeProfile(mockEmployeeProfile());
		verify(userRegisterHelper, times(1)).getListOfGroups();
		verify(employeeProfileDao, times(1)).findByEmail(stringCaptor.capture());
		verify(employeeProfileDao, times(1)).findAndUpdateSequece();
		verify(employeeProfileDao, times(1)).insert(entityCaptor.capture());
		verify(userRegisterHelper, times(1)).registerUser(profileCaptor.capture());
		verifyNoMoreInteractions(employeeProfileDao, employeeProfileDao);
		assertEquals("P000134", entityCaptor.getValue().getEmployeeId());
		assertEquals(mockEmployeeProfile(), profileCaptor.getValue());
		assertEquals("work.mail@pns.com", stringCaptor.getValue());
		assertEquals(mockEmployeeProfile(), employeeProfile);
		mockEmployeeProfileEntity.setEmployeeId("P000134");
		assertTrue(mockEmployeeProfileEntity.equals(entityCaptor.getValue()));
	}

	@Test
	void insertDesigantionIsNotPresentTest() {
		when(userRegisterHelper.getListOfGroups()).thenReturn(Collections.emptyList());
		assertThrows(PnsException.class, () -> service.createEmployeeProfile(mockEmployeeProfile()));
		verify(userRegisterHelper, times(1)).getListOfGroups();
		verify(employeeProfileDao, times(1)).findByEmail(stringCaptor.capture());
		verifyNoMoreInteractions(employeeProfileDao, employeeProfileDao);
		assertEquals("work.mail@pns.com", stringCaptor.getValue());
	}

	@Test
	void insertEmailAlreadyPresentTest() {
		when(employeeProfileDao.findByEmail(Mockito.any()))
				.thenReturn(Optional.of(EmployeeProfileEntity.builder().build()));
		assertThrows(PnsException.class, () -> service.createEmployeeProfile(mockEmployeeProfile()));
		verify(employeeProfileDao, times(1)).findByEmail(stringCaptor.capture());
		verifyNoMoreInteractions(employeeProfileDao, employeeProfileDao);
		assertEquals("work.mail@pns.com", stringCaptor.getValue());
	}

	private EmployeeProfileEntity mockEmployeeProfileEntity() {
		// @formatter:off
		final EmployeeProfileEntity entity = EmployeeProfileEntity.builder().address("address")
				.baseLocation("baselocation").bloodGroup("A+ve").designation("designation")
				.dateOfBirth(LocalDate.parse("2020-12-12")).dateOfJoining(LocalDate.parse("2020-03-12"))
				.employeeId("empid").familyName("familyname").firstName("firstname").gender("Male")
				.image(new byte[] { 1, 2 }).middleName("middlename").mobileNo("1234567890").workContactNo("0987654321")
				.workEmail("work.mail@pns.com").build();
		// @formatter:on
		entity.setId("5f2ea7b9ae64c10bd8383933");
		return entity;
	}

	private EmployeeProfile mockEmployeeProfile() {
		// @formatter:off
		return ImmutableEmployeeProfile.builder().address("address").baseLocation("baselocation").bloodGroup("A+ve")
				.dateOfBirth(LocalDate.parse("2020-12-12")).designation("designation")
				.dateOfJoining(LocalDate.parse("2020-03-12")).employeeId("empid").familyName("familyname")
				.firstName("firstname").gender("Male").image(new byte[] { 1, 2 }).middleName("middlename")
				.mobileNo("1234567890").workContactNo("0987654321").workEmail("work.mail@pns.com")
				.id("5f2ea7b9ae64c10bd8383933").build();
		// @formatter:on
	}

}
