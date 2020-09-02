package com.pns.contractmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.Manager;
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
    
    @Captor
    ArgumentCaptor<byte[]> imageCaptor;
    
    @Captor
    ArgumentCaptor<String>stringCaptor;
    
    @Mock
    EmployeeProfile mockProfile;
    
    @Mock
    Manager mockMangerSearchResult;
    
    @Test
    void getEmployeeProfileTest() {
        when(service.getEmployeeProfile()).thenReturn(mockProfile);
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
    
    @Test
    void findProfileByIdTest() {
        when(service.findProfileById(Mockito.anyString())).thenReturn(mockProfile);
        final EmployeeProfile employee = controller.findProfileById("sampleId");
        verify(service,only()).findProfileById(stringCaptor.capture());
        assertEquals("sampleId", stringCaptor.getValue());
        assertEquals(mockProfile, employee);
    }
    
    @Test
    void uploadImageTest() throws IOException {
        when(service.uploadImage(Mockito.any())).thenReturn(mockProfile);
        MultipartFile image = mock(MultipartFile.class);
        when(image.getBytes()).thenReturn(new byte[] { 1, 2 });
        final EmployeeProfile employee = controller.uploadImage(image);
        verify(image, only()).getBytes();
        verify(service, only()).uploadImage(imageCaptor.capture());
        assertArrayEquals(new byte[] { 1, 2 }, imageCaptor.getValue());
        assertEquals(mockProfile, employee);
    }
    
    @Test
    void searchEmployeeTest() throws IOException {
        when(service.searchManager(Mockito.any())).thenReturn(List.of(mockMangerSearchResult));
        final List<Manager> mangerSearchResult = controller.searchManager("mockQuery");
        verify(service,times(1)).searchManager(stringCaptor.capture());
        assertEquals("mockQuery", stringCaptor.getValue());
        assertIterableEquals(List.of(mockMangerSearchResult), mangerSearchResult);
    }

}
