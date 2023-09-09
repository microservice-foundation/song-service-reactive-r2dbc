package com.epam.training.microservicefoundation.songservice.domain.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class AuditableDTO {
  private String lastModifiedBy;
  private Date lastModifiedDate;
}
