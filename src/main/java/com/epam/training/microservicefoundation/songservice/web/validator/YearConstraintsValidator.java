package com.epam.training.microservicefoundation.songservice.web.validator;

import java.time.Year;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearConstraintsValidator implements ConstraintValidator<ValidYear, Integer> {
  private static final String REGEX_YEAR = "^(19|20)\\d{2}$";

  @Override
  public void initialize(ValidYear constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return String.valueOf(value).matches(REGEX_YEAR) && Year.now().isAfter(Year.of(value));
  }
}
