package com.pns.contractmanagement.helper.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.EmployeeProfile;

@Component
public class UserRegisterHelperImpl {

	
	
	private  final String adminRealmUri = "https://pns.southeastasia.cloudapp.azure.com/auth/admin/realms/PNS_REALM";

	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) {

		return builder.setConnectTimeout(Duration.ofMillis(30000)).setReadTimeout(Duration.ofMillis(30000)).build();
	}

	@Autowired
	private RestTemplate template;

	public void registerUser(final EmployeeProfile profile) {
		try {
			final String accessToken = getAccessToken();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);
			final UserRepresentation user = buildUserRepresentation(profile);
			final HttpEntity<UserRepresentation> createUserRequest = new HttpEntity<UserRepresentation>(user, headers);
			template.postForObject(adminRealmUri+"/users", createUserRequest, Object.class);
		} catch (RestClientException ex) {
			throw new PnsException(ex);
		}
		
	}
	
	public List<String> getListOfGroups() {
		try {
			final String accessToken = getAccessToken();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);
			try {
				final ParameterizedTypeReference<List<GroupRepresentation>> parameterizedTypeReference = new ParameterizedTypeReference<List<GroupRepresentation>>() {};
				final RequestEntity<Void> entity = RequestEntity.get(new URI(adminRealmUri+"/groups")).headers(headers).build();
				ResponseEntity<List<GroupRepresentation>> exchange = 	template.exchange(entity, parameterizedTypeReference);
				return exchange.getBody().stream().map(GroupRepresentation::getName).collect(Collectors.toList());
			} catch (URISyntaxException ex) {
				throw new PnsException(ex);
			}
		} catch (RestClientException ex) {
			throw new PnsException(ex);
		}
		
	}
	
	
	

	private String getAccessToken() {
		return AuthzClient.create().obtainAccessToken("pnsadmin", "pnsadmin@321").getToken();
	}

	private UserRepresentation buildUserRepresentation(final EmployeeProfile profile) {
		final UserRepresentation user = new UserRepresentation();
		user.setFirstName(profile.getFirstName());
		final Map<String, List<String>> attributes = new HashMap<String, List<String>>();
		attributes.put("mobile", List.of(profile.getMobileNo()));
		attributes.put("designation", List.of(profile.getDesignation()));
		attributes.put("dateOfJoining", List.of(profile.getDateOfJoining().format(DateTimeFormatter.ISO_LOCAL_DATE)));
		attributes.put("dateOfBirth", List.of(profile.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE)));
		user.setAttributes(attributes);
		user.setLastName(profile.getFamilyName());
		user.setEmail(profile.getWorkEmail());
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setUsername(profile.getEmployeeId());
		user.setGroups(List.of(profile.getDesignation()));
		final CredentialRepresentation cred = new CredentialRepresentation();
		cred.setType(CredentialRepresentation.PASSWORD);
		cred.setValue("PnsService@"+profile.getMobileNo());
		cred.setTemporary(true);
		user.setCredentials(List.of(cred));
		return user;
	}
}
