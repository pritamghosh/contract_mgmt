package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Updates.set;

import org.bson.Document;
import org.bson.conversions.Bson;

public final class DaoUtil {
	public static final Document NOT_DELETED_FILTER = new Document("deleted", null);
	public static  final Bson DELET_BSON_DOC = set("deleted", true);
	private DaoUtil() {
	}

	public static Document buildCaseInsentiveQuery(final String query) {
		return new Document().append("$regex", "^" + query + "$").append("$options", "i");
	}
	
	public static long countPages(long countDocuments, int pageSize) {
		return  (countDocuments/pageSize)+(countDocuments%pageSize>0?1:0);
	}
}
