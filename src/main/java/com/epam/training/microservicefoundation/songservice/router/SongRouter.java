package com.epam.training.microservicefoundation.songservice.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

import com.epam.training.microservicefoundation.songservice.handler.impl.SongHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SongRouter {
  private static final String QUERY_PARAM_ID = "id";

  @Bean
  RouterFunction<ServerResponse> routes(SongHandler handler) {
    return RouterFunctions.nest(RequestPredicates.path("/api/v1/songs"),
        RouterFunctions
            .route(GET("/{id}").and(accept(APPLICATION_JSON)), handler::getById)
            .andRoute(PUT("/{id}").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), handler::update)
            .andRoute(POST("").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), handler::save)
            .andRoute(DELETE("").and(RequestPredicates.queryParam(QUERY_PARAM_ID, t -> true)).and(accept(APPLICATION_JSON)),
                request -> handler.deleteByIds(request, QUERY_PARAM_ID))
            .andRoute(DELETE("by-resource-id").and(RequestPredicates.queryParam(QUERY_PARAM_ID, t -> true))
                    .and(accept(APPLICATION_JSON)), request -> handler.deleteByIds(request, QUERY_PARAM_ID)));
  }
}
