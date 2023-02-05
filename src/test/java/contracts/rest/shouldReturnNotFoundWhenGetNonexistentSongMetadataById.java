package contracts.rest;

import org.springframework.cloud.contract.spec.Contract;

import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldReturnNotFoundWhenGetNonexistentSongMetadataById implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.description("Represents a not-found scenario of getting a non existent song metadata by id");
            contract.request(request -> {
                request.url("/api/v1/songs/1999");
                request.method(request.GET());
            });
            contract.response(response -> {
                response.status(response.NOT_FOUND());
                response.body(map()
                        .entry("status", "NOT_FOUND")
                        .entry("message", "Song metadata not found")
                        .entry("debugMessage", "Song was not found with id '1999'")
                );
            });
        });
    }
}
