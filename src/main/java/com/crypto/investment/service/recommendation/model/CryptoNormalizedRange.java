package com.crypto.investment.service.recommendation.model;

public class CryptoNormalizedRange implements Comparable<CryptoNormalizedRange> {
    private String cryptoName;
    private Float normalizedRange;

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public Float getNormalizedRange() {
        return normalizedRange;
    }

    public void setNormalizedRange(Float normalizedRange) {
        this.normalizedRange = normalizedRange;
    }

    @Override
    public int compareTo(CryptoNormalizedRange cryptoNormalizedRange) {
        return this.getNormalizedRange().compareTo(cryptoNormalizedRange.getNormalizedRange());
    }
}