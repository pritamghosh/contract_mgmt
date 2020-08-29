package com.pns.contractmanagement.helper.impl;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.EmployeeProfile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Component
public class UserRegisterHelperImpl {

	private static final String ACCESS_CODE_URI = "https://pns.southeastasia.cloudapp.azure.com/auth/realms/master/protocol/openid-connect/token";

	private static final String CREATE_USER_URI = "https://pns.southeastasia.cloudapp.azure.com/auth/admin/realms/PNS_REALM/users";

	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) {

		return builder.setConnectTimeout(Duration.ofMillis(30000)).setReadTimeout(Duration.ofMillis(30000)).build();
	}

	@Autowired
	private RestTemplate template;

	public void registerUser(final EmployeeProfile profile) {
		try {
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			final MultiValueMap<String, String> map = buildRequestForAccessToken();

			final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			final AuthRespone authResp = template.postForObject(ACCESS_CODE_URI, request, AuthRespone.class);
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + authResp.getAccess_token());
			// @formatter:off
			final UserRepresentation user = buildUserRepresentation(profile);
			// @formatter:on
			final HttpEntity<UserRepresentation> createUserRequest = new HttpEntity<UserRepresentation>(user, headers);
			template.postForObject(CREATE_USER_URI, createUserRequest, Object.class);
		} catch (RestClientException ex) {
			throw new PnsException(ex);
		}
		
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
		cred.setValue("pass");
		cred.setTemporary(true);
		user.setCredentials(List.of(cred));
		return user;
	}

	private MultiValueMap<String, String> buildRequestForAccessToken() {
		final MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", "admin-cli");
		map.add("username", "pnsadmin");
		map.add("password", "pnsadmin@321");
		map.add("grant_type", "password");
		return map;
	}

	@Setter
	@Getter
	static class AuthRespone {
		private String access_token;
		private int expires_in;
		private String refresh_token;
		private int refresh_expires_in;
		private String bearer;
		private String snotsession_state;
		private String scope;
	}

	@Setter
	@Getter
	@Builder
	static class KeyCloakUser {
		private String firstName;
		private String lastName;
		private String email;
		private String username;
		@Builder.Default
		private boolean enabled = true;
	}
}
