package com.joonho.spring.demo.dto;

import com.joonho.spring.demo.model.CoinType;
import com.joonho.spring.demo.model.CurrencyType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(indexName = "coin", shards = 1, replicas = 1, refreshInterval = "-1")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Coin {

    @Id
    private String id;

    private CoinType coin_id;
    //private String coin;

    private double price;

    private CurrencyType currency;
    //private String currency;

    private Date timestamp;

    private String comment;
}
