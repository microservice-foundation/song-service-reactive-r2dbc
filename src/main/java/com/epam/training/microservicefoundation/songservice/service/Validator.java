package com.epam.training.microservicefoundation.songservice.service;

public interface Validator<I> {
    boolean validate(I input);
}
