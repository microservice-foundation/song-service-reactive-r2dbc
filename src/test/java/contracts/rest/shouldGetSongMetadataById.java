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
                        .entry("id", 199L)
                        .entry("resourceId", 5L)
                        .entry("name", "Pure Code")
                        .entry("artist", "Tom Ashley")
                        .entry("album", "Software Engineering")
                        .entry("length", "13:56")
                        .entry("year", "2010"));
                response.headers(headers -> headers.contentType(headers.applicationJson()));
            });
        });
    }
}
