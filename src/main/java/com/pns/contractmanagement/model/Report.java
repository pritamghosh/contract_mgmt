package com.pns.contractmanagement.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.springframework.http.MediaType;

/**
 *
 */
@Value.Immutable
public interface Report {
	@Nullable
	String getFileName();

    MediaType getContentType();

    byte[] getContent();

}
