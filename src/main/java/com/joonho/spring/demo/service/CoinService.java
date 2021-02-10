package com.joonho.spring.demo.service;

import com.joonho.spring.demo.config.CoinPriceApiConfig;
import com.joonho.spring.demo.config.HttpClientConfig;
import com.joonho.spring.demo.dto.Coin;
import com.joonho.spring.demo.model.CoinRepository;
import com.joonho.spring.demo.model.CoinType;
import com.joonho.spring.demo.model.CurrencyType;
import org.apache.tomcat.util.json.JSONParser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class CoinService {
    private CoinPriceApiConfig coinPriceApiConfig;
    private RestTemplate restTemplate;
    private ElasticsearchOperations elasticsearchOperations;

    public CoinService(CoinPriceApiConfig coinPriceApiConfig, RestTemplate restTemplate, ElasticsearchOperations elasticsearchOperations) {
        this.coinPriceApiConfig = coinPriceApiConfig;
        this.restTemplate = restTemplate;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public String getCoinPrice(String coinId) {
        HttpHeaders header = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(header);

        ResponseEntity resultMap = restTemplate.exchange(coinPriceApiConfig.getURL(coinId), HttpMethod.GET, entity, String.class);

        try {
            JSONObject jsonObject = new JSONObject(resultMap.getBody().toString());

            Coin coin = new Coin();

            coin.setCoin_id(CoinType.valueOf(coinId));
            coin.setId(UUID.randomUUID().toString());
            coin.setPrice(Double.parseDouble((String) jsonObject.get("lprice")));
            coin.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
            coin.setCurrency(CurrencyType.USD);

            IndexCoordinates indexCoordinates = elasticsearchOperations.getIndexCoordinatesFor(Coin.class);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(coin)
                    .build();

            String documentId = elasticsearchOperations.index(indexQuery, indexCoordinates);
            return documentId;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}