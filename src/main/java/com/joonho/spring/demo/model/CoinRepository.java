package com.joonho.spring.demo.model;

import com.joonho.spring.demo.dto.Coin;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CoinRepository extends ElasticsearchRepository<Coin, String> {
    public List<Coin> findByname(String name);
    public Coin findByemail(String email);
}
