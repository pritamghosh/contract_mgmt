package com.pns.contractmanagement.dao;

import org.bson.Document;

public final class DaoUtil {

	private DaoUtil() {
	}

	public static Document buildCaseInsentiveQuery(final String query) {
		return new Document().append("$regex", "^" + query + "$").append("$options", "i");
	}
}
