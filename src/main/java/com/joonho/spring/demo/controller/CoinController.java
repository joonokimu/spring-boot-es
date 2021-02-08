package com.joonho.spring.demo.controller;

import com.joonho.spring.demo.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @GetMapping("/get/{coinId}")
    public String get(@PathVariable String coinId) {
        String result = coinService.getCoinPrice(coinId);

        return result;
    }
}