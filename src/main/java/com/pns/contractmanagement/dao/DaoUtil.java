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
	public static final Document NOT_DELETED_FILTER = new Document("deleted", null);

	private DaoUtil() {
	}

	public static Document buildCaseInsentiveQuery(final String query) {
		return new Document().append("$regex", "^" + query + "$").append("$options", "i");
	}

	public static long countPages(long countDocuments, int pageSize) {
		return (countDocuments / pageSize) + (countDocuments % pageSize > 0 ? 1 : 0);
	}

	public static <T extends BaseMongoEntity> void setCreationDetails(T entity) {
		entity.setCreationDate(LocalDateTime.now());
		entity.setCreatedBy(getUsernameFromContext());
	}

	public static <T extends BaseMongoEntity> void setModificationDetails(T entity) {
		entity.setLastModifiedBy(getUsernameFromContext());
		entity.setLastModifiedDate(LocalDateTime.now());
	}

	public static String getUsernameFromContext() {
		final KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		@SuppressWarnings("unchecked")
		KeycloakPrincipal<KeycloakSecurityContext> pricipal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication
				.getPrincipal();
		final String name = pricipal.getName();
		return name;
	}

	public static final Bson deleteBsonDoc() {
		return combine(set("lastModifiedBy", LocalDate.now()),
				set("lastModifiedDate", DaoUtil.getUsernameFromContext()), set("deleted", true));
	}
}
