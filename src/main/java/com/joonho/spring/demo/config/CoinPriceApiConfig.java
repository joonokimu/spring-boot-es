package com.joonho.spring.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:api.yml")
public class CoinPriceApiConfig {

    @Value("${bitcoin}")
    private String bitcoin;

    @Value("${ethereum}")
    private String ethereum;

    @Value("${ripple}")
    private String ripple;
}
