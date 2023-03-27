package com.epam.training.microservicefoundation.songservice.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@EnableWebFlux
public class SongRouter {
  @Bean
  RouterFunction<ServerResponse> songRoutes(SongHandler handler) {
    return RouterFunctions.nest(RequestPredicates.path("/api/v1/songs"),
        RouterFunctions
            .route(GET("/{id}"), handler::getById)
            .andRoute(POST(""), handler::save)
            .andRoute(DELETE("").and(RequestPredicates.queryParam("id", t -> true)), handler::deleteByIds)
            .andRoute(DELETE("by-resource-id").and(RequestPredicates.queryParam("id", t -> true)),
                handler::deleteByResourceIds));
  }
}
