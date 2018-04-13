package com.generalbytes.batm.server.extensions.extra.flashcoin.sources.cryptopia;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by vsobalski on 15/02/18.
 */
/**

 {
 "Success":true,
 "Message":null,
 "Data":{
 "TradePairId":5217,
 "Label":"FLASH/BTC",
 "AskPrice":0.00000385,
 "BidPrice":0.00000382,
 "Low":0.00000374,
 "High":0.00000401,
 "Volume":418366.4194124,
 "LastPrice":0.00000385,
 "BuyVolume":3682941.99093377,
 "SellVolume":12813571.80869622,
 "Change":-2.53,
 "Open":0.00000395,
 "Close":0.00000385,
 "BaseVolume":1.60835246,
 "BuyBaseVolume":4.26017443,
 "SellBaseVolume":28700.0528
 },
 "Error":null
 }
**/
public class OrderBookResponse {
    public boolean Success;
    public BigDecimal Message;

    @JsonProperty("Data")
    public  Data Data;
    public BigDecimal Error;

    public BigDecimal getMessage() {
        return Message;
    }

    public void setMessage(BigDecimal message) {
        Message = message;
    }

    public BigDecimal getError() {
        return Error;
    }

    public void setError(BigDecimal error) {
        this.Error = error;
    }

    public Data getData() {
        return Data;
    }

    public void setData(Data Data) {
        this.Data = Data;
    }

    public boolean getSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        this.Success = success;
    }

    public class Data {
        public BigDecimal TradePairId;
        public String Label;
        public BigDecimal AskPrice;
        public BigDecimal BidPrice;
        public BigDecimal Low;
        public BigDecimal High;
        public BigDecimal Volume;
        public BigDecimal LastPrice;
        public BigDecimal BuyVolume;
        public BigDecimal SellVolume;
        public BigDecimal Change;
        public BigDecimal Open;
        public BigDecimal Close;
        public BigDecimal BaseVolume;
        public BigDecimal BuyBaseVolume;
        public BigDecimal SellBaseVolume;

        public BigDecimal getBaseVolume() {
            return BaseVolume;
        }

        public void setBaseVolume(BigDecimal baseVolume) {
            BaseVolume = baseVolume;
        }



        public BigDecimal getTradePairId() {return TradePairId;}
        public void setTradePairId(BigDecimal TradePairId) {this.TradePairId = TradePairId;}

        public String getLabel() {return Label;}
        public void setLabel(String Label) {this.Label = Label;}

        public BigDecimal getAskPrice() {return AskPrice;}
        public void setAskPrice(BigDecimal AskPrice) {this.AskPrice = AskPrice;}

        public BigDecimal getBidPrice() {return BidPrice;}
        public void setBidPrice(BigDecimal BidPrice) {this.BidPrice = BidPrice;}

        public BigDecimal getLow() {return Low;}
        public void setLow(BigDecimal Low) {this.Low = Low;}

        public BigDecimal getHigh() {return High;}
        public void setHigh(BigDecimal High) {this.High = High;}

        public BigDecimal getVolume() {return Volume;}
        public void setVolume(BigDecimal Volume) {this.Volume = Volume;}

        public BigDecimal getLastPrice() {return LastPrice;}
        public void setLastPrice(BigDecimal LastPrice) {this.LastPrice = LastPrice;}

        public BigDecimal getBuyVolume() {return BuyVolume;}
        public void setBuyVolume(BigDecimal BuyVolume) {this.BuyVolume = BuyVolume;}

        public BigDecimal getSellVolume() {return SellVolume;}
        public void setSellVolume(BigDecimal SellVolume) {this.SellVolume = SellVolume;}

        public BigDecimal getChange() {return Change;}
        public void setChange(BigDecimal Change) {this.Change = Change;}

        public BigDecimal getOpen() {return Open;}
        public void setOpen(BigDecimal Open) {this.Open = Open;}

        public BigDecimal getClose() {return Close;}
        public void setClose(BigDecimal Close) {this.Close = Close;}

        public BigDecimal getBuyBaseVolume() {
            return BuyBaseVolume;
        }

        public void setBuyBaseVolume(BigDecimal buyBaseVolume) {
            BuyBaseVolume = buyBaseVolume;
        }

        public BigDecimal getSellBaseVolume() {
            return SellBaseVolume;
        }

        public void setSellBaseVolume(BigDecimal sellBaseVolume) {
            SellBaseVolume = sellBaseVolume;
        }

    }

}
