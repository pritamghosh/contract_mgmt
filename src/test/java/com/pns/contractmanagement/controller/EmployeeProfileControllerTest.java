package com.pns.contractmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.service.EmployeeProfileService;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class EmployeeProfileControllerTest {
    @Mock
    private EmployeeProfileService service;

    @InjectMocks
    private EmployeeProfileController controller;

    @Captor
    ArgumentCaptor<EmployeeProfile> employeeProfileCaptor;
    
    @Mock
    EmployeeProfile mockProfile;
    
    @Test
    void getEmployeeProfileTest() {
        when(service.getEmployeeProfileEmployeeId()).thenReturn(mockProfile);
        final EmployeeProfile employee = controller.getEmployeeProfile();
        assertEquals(mockProfile, employee);
    }
    
    @Test
    void createEmployeeProfieTest() {
        when(service.createEmployeeProfile(Mockito.any(EmployeeProfile.class))).thenReturn(mockProfile);
        final EmployeeProfile mock = mock(EmployeeProfile.class);
        final EmployeeProfile employee = controller.createEmployeeProfie(mock);
        verify(service,only()).createEmployeeProfile(employeeProfileCaptor.capture());
        assertEquals(mock, employeeProfileCaptor.getValue());
        assertEquals(mockProfile, employee);
    }

}
