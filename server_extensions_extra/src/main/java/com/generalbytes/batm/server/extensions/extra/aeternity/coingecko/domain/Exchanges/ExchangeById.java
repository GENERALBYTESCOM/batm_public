package com.generalbytes.batm.server.extensions.extra.aeternity.coingecko.domain.Exchanges;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.extra.aeternity.coingecko.domain.Shared.Ticker;

import java.util.List;

public class ExchangeById extends Exchanges{
    @JsonProperty("tickers")
    private List<Ticker> tickers;
    @JsonProperty("status_updates")
    private List<Object> statusUpdates;

    @Override
    public String getId(){
        return super.getId();
    }

    @Override
    public void setId(String id){
        super.setId(id);
    }
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public long getYearEstablished() {
        return super.getYearEstablished();
    }

    @Override
    public void setYearEstablished(long yearEstablished) {
        super.setYearEstablished(yearEstablished);
    }

    @Override
    public String getCountry() {
        return super.getCountry();
    }

    @Override
    public void setCountry(String country) {
        super.setCountry(country);
    }

    @Override
    public Object getDescription() {
        return super.getDescription();
    }

    @Override
    public void setDescription(Object description) {
        super.setDescription(description);
    }

    @Override
    public String getUrl() {
        return super.getUrl();
    }

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
    }

    @Override
    public String getImage() {
        return super.getImage();
    }

    @Override
    public void setImage(String image) {
        super.setImage(image);
    }

    @Override
    public boolean isHasTradingIncentive() {
        return super.isHasTradingIncentive();
    }

    @Override
    public void setHasTradingIncentive(boolean hasTradingIncentive) {
        super.setHasTradingIncentive(hasTradingIncentive);
    }

    @Override
    public double getTradeVolume24hBtc() {
        return super.getTradeVolume24hBtc();
    }

    @Override
    public void setTradeVolume24hBtc(double tradeVolume24hBtc) {
        super.setTradeVolume24hBtc(tradeVolume24hBtc);
    }

    @JsonProperty("tickers")
    public List<Ticker> getTickers() {
        return tickers;
    }

    @JsonProperty("tickers")
    public void setTickers(List<Ticker> tickers) {
        this.tickers = tickers;
    }

    public List<Object> getStatusUpdates() {
        return statusUpdates;
    }

    public void setStatusUpdates(List<Object> statusUpdates) {
        this.statusUpdates = statusUpdates;
    }

    @Override
    public String toString() {
        return "ExchangeById{" +
                "name='" + name + '\'' +
                ", yearEstablished=" + yearEstablished +
                ", country='" + country + '\'' +
                ", description=" + description +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", hasTradingIncentive=" + hasTradingIncentive +
                ", tradeVolume24hBtc=" + tradeVolume24hBtc +
                ", tickers=" + tickers +
                ", statusUpdates=" + statusUpdates +
                '}';
    }
}
