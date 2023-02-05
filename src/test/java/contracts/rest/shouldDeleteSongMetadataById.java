package contracts.rest;

import org.springframework.cloud.contract.spec.Contract;

import java.util.Collections;
import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldDeleteSongMetadataById implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.description("Represents a successful scenario of deleting a song metadata by id(s)");
            contract.request(request -> {
                request.method(request.DELETE());
                request.url("/api/v1/songs", url -> {
                    url.queryParameters(queryParameters -> {
                        queryParameters.parameter("id", "199");
                    });
                });
                request.headers(headers -> headers.accept(headers.applicationJson()));
            });
            contract.response(response -> {
                response.status(response.OK());
                response.body(Collections.singletonList(map().entry("id", 199L)));
                response.headers(headers -> headers.contentType(headers.applicationJson()));
            });
        });
    }
}
