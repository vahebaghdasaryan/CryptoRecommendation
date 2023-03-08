package com.crypto.investment.service.recommendation.controller;

import com.crypto.investment.service.recommendation.model.CryptoNormalizedRange;
import com.crypto.investment.service.recommendation.model.CryptoOldestNewestMaxMInValues;
import com.crypto.investment.service.recommendation.service.CryptoService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "Crypto API", version = "1.0", description = "Crypto Recommendation Service API"),
        security = {
                @SecurityRequirement(name = "mySecurityScheme")
        })
@RestController
@RequestMapping("/cryptos")
public class CryptoController {
    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/")
    public String getAll() {
        return "getAll";
    }

    /**
     * Endpoint that will return the oldest/newest/min/max values for the given crypto.
     *
     * @param crypto the given crypto
     * @return Oldest/newest/min/max values
     */
    @Operation(summary = "Get the oldest/newest/min/max values", description = "Returns the oldest/newest/min/max values for the given crypto.")
    @Parameters(value = {
            @Parameter(name = "crypto", description = "The given crypto", example = "XRP")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The oldest/newest/min/max values"),
            @ApiResponse(responseCode = "400", description = "Unsupported crypto is provided"),
            @ApiResponse(responseCode = "403", description = "Request is not allowed from this client IP, try from the IP 127.0.0.1")
    })
    @GetMapping("/{crypto}/oldest-newest-min-max")
    public CryptoOldestNewestMaxMInValues getOldestNewestMinMaxValues(@PathVariable("crypto") String crypto) {
        return cryptoService.getCryptoOldestNewestMaxMInValues(crypto);
    }

    /**
     * Endpoint that will return a descending sorted list of all the cryptos, comparing by the normalized range (i.e. (max-min)/min)
     *
     * @return Cryptos list.
     */
    @Operation(summary = "Get the cryptos list", description = "Returns the descending sorted list of all cryptos comparing by the normalized range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptos list"),
            @ApiResponse(responseCode = "403", description = "Request is not allowed from this client IP, try from the IP 127.0.0.1")
    })
    @GetMapping("/descending-normalized")
    public List<CryptoNormalizedRange> getDescendingNormalizedCryptosList() {
        List<CryptoNormalizedRange> cryptoNormalizedRangeList = cryptoService.getCryptoNormalizedRangeList();
        cryptoNormalizedRangeList.sort(Collections.reverseOrder());

        return cryptoNormalizedRangeList;
    }

    /**
     * Endpoint that will return the crypto with the highest normalized range for the given date
     *
     * @param millis the given date as milliseconds
     * @return Crypto
     */
    @Operation(summary = "Get the crypto with the highest normalized range", description = "Returns the crypto with the highest normalized range for the given date.")
    @Parameters(value = {
            @Parameter(name = "millis", description = "The given date as milliseconds", example = "1643205600000")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crypto"),
            @ApiResponse(responseCode = "400", description = "Invalid milliseconds is provided"),
            @ApiResponse(responseCode = "403", description = "Request is not allowed from this client IP, try from the IP 127.0.0.1")
    })
    @GetMapping("/{millis}/highest-normalized-crypto")
    public String getHighestNormalizedCrypto(@PathVariable("millis") String millis) {
        return "Getting Highest Normalized Range Crypto for the " + millis;
    }
}