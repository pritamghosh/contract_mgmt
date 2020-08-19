package com.pns.contractmanagement.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public abstract class BaseMongoEntity extends AbstractMongoEntity {
private LocalDateTime lastModifiedDate;

private LocalDateTime creationDate;

private String createdBy;

private String lastModifiedBy;
}
