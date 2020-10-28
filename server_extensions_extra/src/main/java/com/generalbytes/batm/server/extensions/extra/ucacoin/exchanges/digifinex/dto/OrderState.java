package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
{
  "code": 0,
  "data": [
    {
      "symbol": "BTC_USDT",
      "order_id": "dd3164b333a4afa9d5730bb87f6db8b3",
      "created_date": 1562303547,
      "finished_date": 0,
      "price": 0.1,
      "amount": 1,
      "cash_amount": 1,
      "executed_amount": 0,
      "avg_price": 0,
      "status": 1,
      "type": "buy",
      "kind": "margin"
    }
  ]
}
 **/
public class OrderState {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("finished_date")
    private String finishedDate;

    @JsonProperty("price")
    private float price;

    @JsonProperty("amount")
    private float amount;

    @JsonProperty("cash_amount")
    private float cashAmount;

    @JsonProperty("executed_amount")
    private float executedAmount;

    @JsonProperty("avg_price")
    private float avgPrice;

    @JsonProperty("status")
    private int status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("kind")
    private String kind;


    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFinishedDate() {
        return this.finishedDate;
    }

    public void setFinishedDate(String finishedDate) {
        this.finishedDate = finishedDate;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getCashAmount() {
        return this.cashAmount;
    }

    public void setCashAmount(float cashAmount) {
        this.cashAmount = cashAmount;
    }

    public float getExecutedAmount() {
        return this.executedAmount;
    }

    public void setExecutedAmount(float executedAmount) {
        this.executedAmount = executedAmount;
    }

    public float getAvgPrice() {
        return this.avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    @Override public String toString() {
        return "OrderState{} ";
    }
}

