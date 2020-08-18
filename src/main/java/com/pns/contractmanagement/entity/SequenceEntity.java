package com.pns.contractmanagement.entity;

import java.time.LocalDate;

import org.bson.types.ObjectId;

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
	private ObjectId _id;
	private String squenceType ;
}
