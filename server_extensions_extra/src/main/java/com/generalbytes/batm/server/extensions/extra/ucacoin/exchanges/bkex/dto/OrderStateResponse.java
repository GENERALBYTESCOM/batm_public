package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/*

{
  "msg": "success",
  "code": "0",
  "data":{
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
}

*/
public class OrderStateResponse {
    
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private String code;
    @JsonProperty("data")
    private OrderState orderState;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderState getOrderState() {
        return this.orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    @Override
    public String toString() {
        return "{" +
            " msg='" + msg + "'" +
            ", code='" + code + "'" +
            ", orderState='" + orderState + "'" +
            "}";
    }

}
