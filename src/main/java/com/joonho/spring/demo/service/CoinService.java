package com.joonho.spring.demo.service;

import com.joonho.spring.demo.config.CoinPriceApiConfig;
import com.joonho.spring.demo.config.HttpClientConfig;
import com.joonho.spring.demo.dto.Coin;
import com.joonho.spring.demo.model.CoinRepository;
import com.joonho.spring.demo.model.CoinType;
import com.joonho.spring.demo.model.CurrencyType;
import org.apache.tomcat.util.json.JSONParser;
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

import java.time.LocalDate;
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

        ResponseEntity resultMap = restTemplate.exchange(coinPriceApiConfig.getBitcoin(), HttpMethod.GET, entity, String.class);

        try {
            JSONArray jsonArray = new JSONArray(resultMap.getBody().toString());
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            Coin coin = new Coin();
            coin.setCoin_id(CoinType.valueOf((String) jsonObject.get("id")));
            coin.setId(UUID.randomUUID().toString());
            coin.setPrice(Double.parseDouble((String) jsonObject.get("price")));
            LocalDateTime temp = LocalDateTime.parse(((String)jsonObject.get("price_date")), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            coin.setTimestamp(temp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
            coin.setCurrency(CurrencyType.USD);

            System.out.println(coin);

            IndexCoordinates indexCoordinates = elasticsearchOperations.getIndexCoordinatesFor(Coin.class);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    //.withId(UUID.randomUUID().toString())
                    .withObject(coin)
                    .build();

//            elasticsearchOperations.index()
            String documentId = elasticsearchOperations.index(indexQuery, indexCoordinates);
            return documentId;
         /*   Coin coin = new Coin();
            coin.setCoin(CoinType.valueOf((String) jsonObject.get("id")));
            coin.setPrice((Double) jsonObject.get("price"));
            coin.setTimestamp((Date) jsonObject.get("price_date"));
            coin.setCurrency(CurrencyType.KRW);

            coinRepository.save(coin);*/

           // return resultMap.getBody().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}