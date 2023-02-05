package contracts.rest;

import org.springframework.cloud.contract.spec.Contract;

import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldReturnBadRequestWhenSaveSongMetadata implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.description("Represents a bad-request scenario of saving a song metadata");
            contract.request(request -> {
                request.method(request.POST());
                request.url("/api/v1/songs");
                request.body(map()
                        .entry("resourceId", 1L)
                        .entry("name", "New office")
                        .entry("artist", "John Kennedy")
                        .entry("album", "ASU")
                        .entry("length", "03:22")
                        .entry("year", 2099)
                );
                request.headers(headers -> headers.contentType(headers.applicationJson()));
            });
            contract.response(response -> {
                response.status(response.BAD_REQUEST());
                response.body(map()
                        .entry("status", "BAD_REQUEST")
                        .entry("message", "Invalid request")
                        .entry("debugMessage", "Saving invalid song record")
                );
            });
        });
    }
}
