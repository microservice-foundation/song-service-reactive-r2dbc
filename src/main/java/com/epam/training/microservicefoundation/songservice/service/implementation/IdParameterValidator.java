package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IdParameterValidator implements Validator<long[]> {
    private static final Logger log = LoggerFactory.getLogger(IdParameterValidator.class);
    @Override
    public boolean validate(long[] input) {
        log.info("Validating a multipart csv file");

        return input != null && input.length < 200;
    }
}
