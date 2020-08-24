package com.pns.contractmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Range;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.ImmutableReport;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.ContractService;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class ContractControllerTest {
    @Mock
    private ContractService service;

    @InjectMocks
    private ContractController controller;

    @Captor
    ArgumentCaptor<String> paramOneCaptor;

    @Captor
    ArgumentCaptor<Range<LocalDate>> rangeCaptor;

    @Captor
    ArgumentCaptor<Integer> pageCaptor;
    
    @Captor
    ArgumentCaptor<Contract> contractCaptor;
    @Mock
    SearchResponse<Contract> mockSearchResp;

    @Mock
    Contract mockContract;

    @Test
    void getContractByProposalNoTest() {
        when(service.getContractByProposalNo(Mockito.anyString())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> contractResp = controller.getContractByProposalNo("SAMPLE_PROPOSAL_NO");
        verify(service, only()).getContractByProposalNo(paramOneCaptor.capture());
        assertEquals(mockSearchResp, contractResp);
        assertEquals("SAMPLE_PROPOSAL_NO", paramOneCaptor.getValue());
    }

    @Test
    void getContractByIdTest() {
        when(service.getContractById(Mockito.anyString())).thenReturn(mockContract);
        final Contract contractResp = controller.getContractById("SAMPLE_CONTRACT_ID");
        verify(service, only()).getContractById(paramOneCaptor.capture());
        assertEquals("SAMPLE_CONTRACT_ID", paramOneCaptor.getValue());
        assertEquals(mockContract, contractResp);
    }
    
    @Test
    void deleteContractTest() {
        when(service.deleteContractById(Mockito.anyString())).thenReturn(mockContract);
        final Contract contractResp = controller.deleteContract("SAMPLE_CONTRACT_ID");
        verify(service, only()).deleteContractById(paramOneCaptor.capture());
        assertEquals("SAMPLE_CONTRACT_ID", paramOneCaptor.getValue());
        assertEquals(mockContract, contractResp);
    }
    
    @Test
    void modifyContractTest() {
        when(service.modifyContract(any(Contract.class))).thenReturn(mockContract);
        final Contract mock = mock(Contract.class);
        final Contract contractResp = controller.modifyContract(mock);
        verify(service, only()).modifyContract(contractCaptor.capture());
        assertEquals(mock, contractCaptor.getValue());
        assertEquals(mockContract, contractResp);
    }
    
    @Test
    void addContractTest() {
        final byte[] mockResp = new byte[] { 0, 1 };
        final Report mockReport = ImmutableReport.builder().content(mockResp).contentType(MediaType.APPLICATION_PDF)
            .fileName("filename").build();
        when(service.addContract(any(Contract.class))).thenReturn(mockReport);
        final Contract mock = mock(Contract.class);
        final ResponseEntity<byte[]> contractResp = controller.addContract(mock);
        verify(service, only()).addContract(contractCaptor.capture());
        assertEquals(mock, contractCaptor.getValue());
        assertArrayEquals(mockResp, contractResp.getBody());
        assertEquals(MediaType.APPLICATION_PDF, contractResp.getHeaders().getContentType());
    }


    @Test
    void getContractsByCustomerIdTest() {
        when(service.getContractsByCustomerId(anyString(), anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.getContractsByCustomerId("customerId", 3);
        verify(service, only()).getContractsByCustomerId(paramOneCaptor.capture(), pageCaptor.capture());
        assertEquals("customerId", paramOneCaptor.getValue());
        assertEquals(3, pageCaptor.getValue());
        assertEquals(mockSearchResp, searchResp);
    }

    @Test
    void getContractsByEquipmentrIdTest() {
        when(service.getContractsByEquipmentrId(anyString(), anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.getContractsByEquipmentrId("equipmentId", 3);
        verify(service, only()).getContractsByEquipmentrId(paramOneCaptor.capture(), pageCaptor.capture());
        assertEquals("equipmentId", paramOneCaptor.getValue());
        assertEquals(3, pageCaptor.getValue());
        assertEquals(mockSearchResp, searchResp);
    }

    @Test
    void getContractPdfByIdTest() {
        final byte[] mockResp = new byte[] { 0, 1 };
        final Report mockReport = ImmutableReport.builder().content(mockResp).contentType(MediaType.APPLICATION_PDF)
            .fileName("filename").build();
        when(service.getContractReportById(Mockito.anyString())).thenReturn(mockReport);
        final ResponseEntity<byte[]> contractResp = controller.getContractPdfById("SAMPLE_CONTRACT_ID");
        verify(service, only()).getContractReportById(paramOneCaptor.capture());
        assertArrayEquals(mockResp, contractResp.getBody());
        assertEquals(MediaType.APPLICATION_PDF, contractResp.getHeaders().getContentType());
        assertEquals("SAMPLE_CONTRACT_ID", paramOneCaptor.getValue());
    }

    @Test
    void getContractsByAmcStartDateRangeTest() {
        when(service.getContractByAmcStartDateRange(any(Range.class), Mockito.anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.getContractsByAmcStartDateRange("2019-01-01",
            "2019-12-31", 5);
        verify(service, only()).getContractByAmcStartDateRange(rangeCaptor.capture(), pageCaptor.capture());
        assertEquals(mockSearchResp, searchResp);
        assertEquals(Range.closed(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)), rangeCaptor.getValue());
        assertEquals(5, pageCaptor.getValue());
    }

    @Test
    void getContractsByCreationDateTest() {
        when(service.getContractcByCreationDateRange(any(Range.class), Mockito.anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.getContractsByCreationDate("2019-01-01", "2019-12-31",
            5);
        verify(service, only()).getContractcByCreationDateRange(rangeCaptor.capture(), pageCaptor.capture());
        assertEquals(mockSearchResp, searchResp);
        assertEquals(Range.closed(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 31)), rangeCaptor.getValue());
        assertEquals(5, pageCaptor.getValue());
    }

    @Test
    void getAllContractTest() {
        when(service.getAllContract(Mockito.anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.getAllContract(2);
        verify(service, only()).getAllContract(pageCaptor.capture());
        assertEquals(mockSearchResp, searchResp);
        assertEquals(2, pageCaptor.getValue());
    }
    
    @Test
    void searchContractByQueryTest() {
        when(service.searchContractByQuery(anyString(), anyInt())).thenReturn(mockSearchResp);
        final SearchResponse<Contract> searchResp = controller.searchContractByQuery("query", 6);
        verify(service, only()).searchContractByQuery(paramOneCaptor.capture(), pageCaptor.capture());
        assertEquals("query", paramOneCaptor.getValue());
        assertEquals(6, pageCaptor.getValue());
        assertEquals(mockSearchResp, searchResp);
    }


}
