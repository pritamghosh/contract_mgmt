package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.pns.contractmanagement.entity.BaseMongoEntity;

public final class DaoUtil {

	private DaoUtil() {
	}

	public static Document notDeletedFilter() {
		return new Document("deleted", null);
	}

	public static Document buildCaseInsentiveQuery(final String query) {
		return new Document().append("$regex", "^" + query + "$").append("$options", "i");
	}

	public static long countPages(final long countDocuments, final int pageSize) {
		return (countDocuments / pageSize) + (countDocuments % pageSize > 0 ? 1 : 0);
	}

	public static <T extends BaseMongoEntity> void setCreationDetails(final T entity) {
		entity.setCreationDate(LocalDateTime.now());
		entity.setCreatedBy(getUsernameFromContext());
	}

	public static <T extends BaseMongoEntity> void setModificationDetails(final T entity) {
		entity.setLastModifiedBy(getUsernameFromContext());
		entity.setLastModifiedDate(LocalDateTime.now());
	}

	public static String getUsernameFromContext() {
		final KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		@SuppressWarnings("unchecked")
		final KeycloakPrincipal<KeycloakSecurityContext> pricipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication
				.getPrincipal();
		return pricipal.getName();
	}

	public static Bson deleteBsonDoc() {
		return combine(set("lastModifiedBy", LocalDate.now()),
				set("lastModifiedDate", DaoUtil.getUsernameFromContext()), set("deleted", true));
	}
}
