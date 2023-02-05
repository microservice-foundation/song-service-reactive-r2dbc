package contracts.rest;

import org.springframework.cloud.contract.spec.Contract;

import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldGetSongMetadataById implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.description("Represents a successful scenario of getting a song metadata by id");
            contract.request(request -> {
                request.method(request.GET());
                request.url("/api/v1/songs/199");
                request.headers(headers -> headers.accept(headers.applicationJson()));
            });
            contract.response(response -> {
                response.status(response.OK());
                response.body(map()
                        .entry("resourceId", 1L)
                        .entry("name", "Saturday")
                        .entry("artist", "John Biden")
                        .entry("album", "2023")
                        .entry("length", "03:40")
                        .entry("year", "1990"));
                response.headers(headers -> headers.contentType(headers.applicationJson()));
            });
        });
    }
}
