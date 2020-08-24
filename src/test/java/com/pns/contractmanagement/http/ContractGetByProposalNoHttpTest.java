package com.pns.contractmanagement.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.pns.contractmanagement.model.Contract;

/**
 *
 */
public class ContractGetByProposalNoHttpTest extends ContractHttpTest {

    @Captor
    ArgumentCaptor<String> proposalNoCaptor;

    @Captor
    ArgumentCaptor<Integer> pageCaptor;

    // @Test
    @WithMockUser(value = "user", roles = "read")
    void getByPrposalNoTest() throws Exception {
        Contract mockContract = null;
        when(service.getContractByProposalNo(Mockito.anyString())).thenReturn(mockContract);
        // @formatter:off
 

        mvc.perform(
            get("/")
            .queryParam("proposalNo", "SAMPLE_PROPOSAL_NO")
            .characterEncoding("utf-8")
            .sessionAttr(tokenName, csrfToken)
            .param(csrfToken.getParameterName(), csrfToken.getToken())
        )
       .andExpect(status().isOk())
       .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // @formatter:on
        verify(service, only()).getContractByProposalNo(proposalNoCaptor.capture());
        assertEquals("SAMPLE_PROPOSAL_NO", proposalNoCaptor.getValue());
    }

    // @Test
    // @WithAnonymousUser
    // void getByPrposalNoAnonymousUserTest() throws Exception {
    //
    // mvc.perform(post("/basic")
    // .contentType(MediaType.APPLICATION_JSON)
    // // .content("{\"query\": \"JUNK\", \"domain\": \"EVENTS\",
    // \"requiredFields\": " + toJsonArray(allFieldsAsKeys()) + "}")
    // .characterEncoding("utf-8").sessionAttr(tokenName, csrfToken)
    // .param(csrfToken.getParameterName(), csrfToken.getToken()))
    // .andExpect(status().isUnauthorized());
    // }
}
