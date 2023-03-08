package com.crypto.investment.service.recommendation.service;

import com.crypto.investment.service.recommendation.model.CryptoNormalizedRange;
import com.crypto.investment.service.recommendation.model.CryptoOldestNewestMaxMInValues;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CryptoService {
    private static int OLDEST_KEY = 1;
    private static int NEWEST_KEY = 2;
    private static int MAX_KEY = 3;
    private static int MIN_KEY = 4;
    private static int NORMALIZED_RANGE = 5;
    private Map<String, Map<Integer, Float>> cryptoMetricsMap;

    @PostConstruct
    private void loadCryptoMetric() throws IOException {
        cryptoMetricsMap = new HashMap<>();
        Resource cryptoValuesDirectory = new ClassPathResource("/static/prices");
        File[] cryptoValuesFilesArray = cryptoValuesDirectory.getFile().listFiles();

        for (File cryptoValuesFile : cryptoValuesFilesArray) {
            Map<Integer, Float> metricsMap = calculateCryptoMetrics(cryptoValuesFile);
            cryptoMetricsMap.put(cryptoValuesFile.getName().split("_")[0], metricsMap);
        }
    }

    private Map<Integer, Float> calculateCryptoMetrics(File cryptoValuesFile) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(cryptoValuesFile.getPath()));

        Map<Integer, Float> cryptoMetrics = new HashMap<>();

        if (!ObjectUtils.isEmpty(lines)) {
            lines.remove(0);//removing file header

            cryptoMetrics.put(OLDEST_KEY, calculateOldestValue(lines));
            cryptoMetrics.put(NEWEST_KEY, calculateNewestValue(lines));
            cryptoMetrics.put(MAX_KEY, calculateMaxValue(lines));
            cryptoMetrics.put(MIN_KEY, calculateMinValue(lines));
            cryptoMetrics.put(NORMALIZED_RANGE, calculateNormalizedRangeValue(lines));
        }

        return cryptoMetrics;
    }

    private Float calculateOldestValue(List<String> lines) {
        return lines.stream()
                .min((lineOne, lineTwo) -> {
                    Long millisecondsOne = Long.valueOf(lineOne.split(",")[0]);
                    Long millisecondsTwo = Long.valueOf(lineTwo.split(",")[0]);

                    return millisecondsOne.compareTo(millisecondsTwo);
                })
                .map(line -> line.split(",")[2])
                .map(Float::valueOf)
                .orElse(null);
    }

    private Float calculateNewestValue(List<String> lines) {
        return lines.stream()
                .max((lineOne, lineTwo) -> {
                    Long millisecondsOne = Long.valueOf(lineOne.split(",")[0]);
                    Long millisecondsTwo = Long.valueOf(lineTwo.split(",")[0]);

                    return millisecondsOne.compareTo(millisecondsTwo);
                })
                .map(line -> line.split(",")[2])
                .map(Float::valueOf)
                .orElse(null);
    }

    private Float calculateMaxValue(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(",")[2])
                .map(Float::valueOf)
                .max(Float::compare).orElse(null);
    }

    private Float calculateMinValue(List<String> lines) {
        return lines.stream()
                .map(line -> line.split(",")[2])
                .map(Float::valueOf)
                .min(Float::compare).orElse(null);
    }

    private Float calculateNormalizedRangeValue(List<String> lines) {
        Float maxValue = calculateMaxValue(lines);
        Float minValue = calculateMinValue(lines);

        return (maxValue - minValue) / minValue;
    }

    public CryptoOldestNewestMaxMInValues getCryptoOldestNewestMaxMInValues(String crypto) {
        Map<Integer, Float> metricsMap = cryptoMetricsMap.get(crypto);

        CryptoOldestNewestMaxMInValues cryptoOldestNewestMaxMInValues = new CryptoOldestNewestMaxMInValues();
        cryptoOldestNewestMaxMInValues.setOldest(metricsMap.get(OLDEST_KEY));
        cryptoOldestNewestMaxMInValues.setNewest(metricsMap.get(NEWEST_KEY));
        cryptoOldestNewestMaxMInValues.setMax(metricsMap.get(MAX_KEY));
        cryptoOldestNewestMaxMInValues.setMin(metricsMap.get(MIN_KEY));

        return cryptoOldestNewestMaxMInValues;
    }

    public List<CryptoNormalizedRange> getCryptoNormalizedRangeList() {
        List<CryptoNormalizedRange> cryptoNormalizedRangeList = new ArrayList<>();

        for (Map.Entry<String, Map<Integer, Float>> cryptoMetricsMapEntry : cryptoMetricsMap.entrySet()) {
            Map<Integer, Float> metricsMap = cryptoMetricsMapEntry.getValue();

            CryptoNormalizedRange cryptoNormalizedRange = new CryptoNormalizedRange();
            cryptoNormalizedRange.setCryptoName(cryptoMetricsMapEntry.getKey());
            cryptoNormalizedRange.setNormalizedRange(metricsMap.get(NORMALIZED_RANGE));

            cryptoNormalizedRangeList.add(cryptoNormalizedRange);
        }

        return cryptoNormalizedRangeList;
    }

    public Float getOldestValue(String crypto) {
        return cryptoMetricsMap.get(crypto).get(OLDEST_KEY);
    }

    public Float getNewestValue(String crypto) {
        return cryptoMetricsMap.get(crypto).get(NEWEST_KEY);
    }

    public Float getMaxValue(String crypto) {
        return cryptoMetricsMap.get(crypto).get(MAX_KEY);
    }

    public Float getMinValue(String crypto) {
        return cryptoMetricsMap.get(crypto).get(MIN_KEY);
    }
}