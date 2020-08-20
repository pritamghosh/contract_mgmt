package com.pns.contractmanagement.entity;

import java.time.LocalDate;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SequenceEntity {
	private int sequence;
	private LocalDate date;
	@BsonId
	@JsonProperty("_id")
	private ObjectId oid;
	private String squenceType;
}
