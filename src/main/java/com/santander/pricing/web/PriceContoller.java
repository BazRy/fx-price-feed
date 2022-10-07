package com.santander.pricing.web;

import com.google.gson.Gson;
import com.santander.pricing.process.PriceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Basic rest endpoint to allow calls for the latest prices per instrument
 */
@RestController
public class PriceContoller {

    private final PriceProcessor priceProcessor;
    private final Gson gson = new Gson();

    @Autowired
    public PriceContoller(PriceProcessor priceProcessor) {
        this.priceProcessor = priceProcessor;
    }

    @GetMapping("/prices")
    public ResponseEntity<String> getLatestPrices() {
        final String json = new Gson().toJson(priceProcessor.getAllPrices());
        return ResponseEntity
                .status(OK)
                .contentType(APPLICATION_JSON)
                .body(json);
    }
}
