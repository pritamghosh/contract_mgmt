package com.pns.contractmanagement.util;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public final class ServiceUtil {

	private ServiceUtil() {

	}

	public static String getUsernameFromContext() {
		final KeycloakPrincipal<KeycloakSecurityContext> principal = getPrincipal();
		return principal.getName();
	}

	public static boolean isUserauthorized(final String role) {
		final KeycloakPrincipal<KeycloakSecurityContext> principal = getPrincipal();
		return principal.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles().contains(role);
	}
	

	private static KeycloakPrincipal<KeycloakSecurityContext> getPrincipal() {
		final KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		@SuppressWarnings("unchecked")
		final KeycloakPrincipal<KeycloakSecurityContext> pricipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication
				.getPrincipal();
		return pricipal;
	}
	

}
