package com.epam.training.microservicefoundation.songservice.configuration;

import com.epam.training.microservicefoundation.songservice.handler.SongExceptionHandler;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.validator.IdQueryParamValidator;
import com.epam.training.microservicefoundation.songservice.validator.RequestBodyValidator;
import com.epam.training.microservicefoundation.songservice.validator.RequestQueryParamValidator;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
@EnableConfigurationProperties(WebProperties.class)
public class WebFluxConfiguration {

  // @Order(Ordered.HIGHEST_PRECEDENCE) on ExceptionHandler class in Spring is used to define the order in which multiple exception handler classes get executed.
  // When multiple exception handler classes are present, the one with the highest precedence will be executed first.
  // The Ordered.HIGHEST_PRECEDENCE constant is used to set the order of the bean to the highest possible value. This ensures that the exception handler gets executed before any other error handling method, even the default Spring error handler.
  // This can be important if there are multiple exception handlers present and you want to ensure that a specific handler gets executed before any other.
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Bean
  public SongExceptionHandler songExceptionHandler(WebProperties webProperties, ApplicationContext applicationContext,
      ServerCodecConfigurer configurer) {
    SongExceptionHandler exceptionHandler =
        new SongExceptionHandler(new DefaultErrorAttributes(), webProperties.getResources(), applicationContext);

    exceptionHandler.setMessageReaders(configurer.getReaders());
    exceptionHandler.setMessageWriters(configurer.getWriters());
    return exceptionHandler;
  }

  @Bean
  public Validator springValidator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public RequestBodyValidator<SaveSongDTO> requestBodyValidator(Validator validator) {
    return new RequestBodyValidator<>(validator, SaveSongDTO.class);
  }

  @Bean
  public RequestQueryParamValidator requestQueryParamValidator(IdQueryParamValidator idQueryParamValidator) {
    return new RequestQueryParamValidator(idQueryParamValidator);
  }
}
