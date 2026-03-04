package com.cryptotracker.cryptotracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CryptoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Double> prices = new TreeMap<>();
    
    private final Map<String, List<Double>> historyCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000;

    private final Map<String, String> coinIdMap = Map.of(
        "BTC", "bitcoin",
        "ETH", "ethereum",
        "SOL", "solana",
        "ADA", "cardano",
        "DOT", "polkadot",
        "DOGE", "dogecoin",
        "XRP", "ripple",
        "LINK", "chainlink"
    );

    public void updatePrices() {
        try {
            String ids = String.join(",", coinIdMap.values());
            String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + ids + "&vs_currencies=usd";
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            coinIdMap.forEach((ticker, id) -> {
                double price = root.path(id).path("usd").asDouble();
                if (price > 0) prices.put(ticker, price);
            });
        } catch (Exception e) {
            System.err.println("Ошибка обновления цен: " + e.getMessage());
        }
    }

    public Map<String, Double> getAllPrices() {
        if (prices.isEmpty()) updatePrices();
        return prices;
    }

    public List<Double> getCoinHistory(String ticker, int days) {
        String cacheKey = ticker.toUpperCase() + "_" + days;
        long now = System.currentTimeMillis();

        if (historyCache.containsKey(cacheKey) && (now - cacheTimestamps.get(cacheKey) < CACHE_DURATION)) {
            System.out.println("LOG: Берем данные из кэша для " + ticker);
            return historyCache.get(cacheKey);
        }

        try {
            System.out.println("LOG: Делаем реальный запрос к API для " + ticker);
            String id = coinIdMap.getOrDefault(ticker.toUpperCase(), "bitcoin");
            String url = String.format("https://api.coingecko.com/api/v3/coins/%s/market_chart?vs_currency=usd&days=%d", id, days);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            List<Double> history = new ArrayList<>();
            JsonNode pricesNode = root.path("prices");
            if (pricesNode.isMissingNode()) return Collections.emptyList();

            for (JsonNode node : pricesNode) {
                history.add(node.get(1).asDouble());
            }

            if (!history.isEmpty()) {
                historyCache.put(cacheKey, history);
                cacheTimestamps.put(cacheKey, now);
            }
            return history;
        } catch (Exception e) {
            System.err.println("API Error for " + ticker + ": " + e.getMessage());

            return historyCache.getOrDefault(cacheKey, Collections.emptyList());
        }
    }
}