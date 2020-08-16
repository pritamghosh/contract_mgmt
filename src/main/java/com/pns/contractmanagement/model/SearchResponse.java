package com.pns.contractmanagement.model;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface SearchResponse<T> {
	List<T> getResult();

	Number getPageCount();
}
