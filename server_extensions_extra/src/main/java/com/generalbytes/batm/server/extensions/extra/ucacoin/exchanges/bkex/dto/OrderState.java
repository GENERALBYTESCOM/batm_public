package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/*



{
      "id": "201806232342422123123",
      "symbol": "BTC_USDT",
      "type": "STOP_LIMIT",
      "totalVolume": 1.123,
      "price": "7000.3241",
      "direction": "ASK",
      "dealVolume": 0.1,
      "frozenVolumeByOrder": 1.123,
      "source": "WALLET",
      "stopPrice": 6900,
      "operator": ">=",
      "status": 0,
      "createdTime": 1530604762277,
      "updateTime": 1530604762277
    }
    */

public class OrderState {

    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("type")
    private String type;

    @JsonProperty("totalvolume")
    private BigDecimal totalVolume;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("dealVolume")
    private BigDecimal dealVolume;

    @JsonProperty("frozenVolumeByOrder")
    private BigDecimal frozenVolumeByOrder;

    @JsonProperty("source")
    private String source;

    @JsonProperty("stopPrice")
    private BigDecimal stopPrice;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("status")
    private int status;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getTotalVolume() {
        return this.totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BigDecimal getDealVolume() {
        return this.dealVolume;
    }

    public void setDealVolume(BigDecimal dealVolume) {
        this.dealVolume = dealVolume;
    }

    public BigDecimal getFrozenVolumeByOrder() {
        return this.frozenVolumeByOrder;
    }

    public void setFrozenVolumeByOrder(BigDecimal frozenVolumeByOrder) {
        this.frozenVolumeByOrder = frozenVolumeByOrder;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getStopPrice() {
        return this.stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + id + "'" +
            ", symbol='" + symbol + "'" +
            ", type='" + type + "'" +
            ", totalVolume='" + totalVolume + "'" +
            ", price='" + price + "'" +
            ", direction='" + direction + "'" +
            ", dealVolume='" + dealVolume + "'" +
            ", frozenVolumeByOrder='" + frozenVolumeByOrder + "'" +
            ", source='" + source + "'" +
            ", stopPrice='" + stopPrice + "'" +
            ", operator='" + operator + "'" +
            ", status='" + status + "'" +
            "}";
    }

    
}
