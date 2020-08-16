package com.pns.contractmanagement.dao;

import org.bson.Document;

public final class DaoUtil {

	private DaoUtil() {
	}

	public static Document buildCaseInsentiveQuery(final String query) {
		return new Document().append("$regex", "^" + query + "$").append("$options", "i");
	}
	
	public static long countPages(long countDocuments, int pageSize) {
		return  (countDocuments/pageSize)+(countDocuments%pageSize>0?1:0);
	}
}
