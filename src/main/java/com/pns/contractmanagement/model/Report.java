package com.pns.contractmanagement.model;

import org.immutables.value.Value;
import org.springframework.http.MediaType;

/**
 *
 */
@Value.Immutable
public interface Report {
    String getFileName();

    MediaType getContentType();

    byte[] getContent();

}
