package com.payment.merchant_service.controller;

import com.payment.merchant_service.dto.CheckoutRequest;
import com.payment.merchant_service.dto.CheckoutResponse;
import com.payment.merchant_service.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService service;

    @PostMapping
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request) {
        return service.checkout(request);
    }
}
