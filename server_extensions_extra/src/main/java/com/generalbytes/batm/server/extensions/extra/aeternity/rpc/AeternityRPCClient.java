/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.aeternity.rpc;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.aeternity.AeternityWallet;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.SpendTransaction;

import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;
import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;

import wf.bitcoin.krotjson.HexCoder;

public class AeternityRPCClient extends RPCClient /* extends BitcoinJSONRPCClient */ {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.RPCClient");
    //private String cryptoCurrency = CryptoCurrency.AE.getCode();
    private final AeternityV2API api;
    
    
    final static String baseUrl = "https://sdk-mainnet.aepps.com/v2"; // default: https://sdk-mainnet.aepps.com/v2
    final com.kryptokrauts.aeternity.sdk.constants.Network mainnet = com.kryptokrauts.aeternity.sdk.constants.Network.MAINNET; 
    private static ServiceConfiguration serviceConf;
    private static AccountService accountService;
    private static ChainService chainService;
    private static TransactionService transactionService;
    private static BigDecimal divider = new BigDecimal("1000000000000000000");
    
    public AeternityRPCClient(String rpcUrl) throws MalformedURLException {
    	super(CryptoCurrency.AE.getCode(), baseUrl);
        if (rpcUrl == null) rpcUrl = baseUrl;
        api = RestProxyFactory.createProxy(AeternityV2API.class, "https://roma-net.mdw.aepps.com");// aeternity official block explorer api, https://explorer.aepps.com
        serviceConf = ServiceConfiguration.configure().baseUrl(rpcUrl).compile();
    	accountService = new AccountServiceFactory().getService(serviceConf);
    	chainService = new ChainServiceFactory().getService(serviceConf);
    	transactionService = new TransactionServiceFactory().getService(TransactionServiceConfiguration.configure().baseUrl(rpcUrl).network(mainnet).compile());
    }
    
	public BigDecimal getAccountBalance(String address) {
    	BigDecimal returnBalance = BigDecimal.ZERO;
    	try {
			Account account = accountService.getAccount(address).blockingGet();
			
			BigInteger balance = account.getBalance();
			if (balance.signum() == 1) {
				returnBalance = new BigDecimal(balance);
				returnBalance = returnBalance.divide(divider);
	    	}
    	}catch (Exception e) {
    		if (e.getMessage().contains("Not Found")) return BigDecimal.ZERO;
            log.error("Error reading balance.", e);
            return null;
		}
    	return returnBalance;
    }
    
	public List<String> getAddressTxIds(String address) throws GenericRpcException {
		List<String> txIds = new ArrayList<String>();
		Object txidsObject;
		try {
			txidsObject = api.getAccountTXs(address);
			List<JSONObject> txidsObjectJSONarray = AeternityRPCClient.convertToJsonObjectArray(txidsObject);
			for (int i=0; i< txidsObjectJSONarray.size(); i++) {
				JSONObject txidObjectJSON = txidsObjectJSONarray.get(i);
				JSONObject tx = txidObjectJSON.getJSONObject("tx");
				if (tx.getString("type").equals("SpendTx") && tx.getString("recipient_id").equals(address)) {
					txIds.add(txidObjectJSON.getString("hash"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
		return txIds;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public NetworkInfo getNetworkInfo() throws GenericRpcException {
		NetworkInfo networkInfo = null;
		Object statusObject;
		try {
			statusObject = api.getStatus();
			JSONObject statusObjectJSON = AeternityRPCClient.convertToJsonObject(statusObject);
			JSONObject rootJsonObject = new JSONObject();
			rootJsonObject.put("connections", statusObjectJSON.getInt("peer_count"));
			HashMap<String,Object> result = new HashMap<String, Object>();
			result = new ObjectMapper().readValue(rootJsonObject.toString(), HashMap.class);
			networkInfo = new NetworkInfoWrapper((Map) result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
		

		return networkInfo;
	}



	@Override
	public String getNewAddress() throws GenericRpcException {
		try {
		AeternityWallet newWallet = new AeternityWallet(null);
		newWallet.saveAeternityWallet();
		return newWallet.getCryptoAddress(CryptoCurrency.AE.getCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public Transaction getTransaction(String txId) {
		GenericSignedTx transaction = transactionService.getTransactionByHash(txId).blockingGet();
		JSONObject rootJsonObject = new JSONObject();
		rootJsonObject.put("confirmations",getBlockCount() - transaction.getBlockHeight().intValue());
		rootJsonObject.put("blockhash",transaction.getBlockHash());
		rootJsonObject.put("txid",transaction.getHash());
		
		GenericTx tx = transaction.getTx();
		if (tx instanceof SpendTx) {
			SpendTx spendTx = (SpendTx)tx;
			BigDecimal amount = new BigDecimal(spendTx.getAmount());
			BigDecimal fee = new BigDecimal(spendTx.getFee());
			rootJsonObject.put("amount",amount.divide(divider));
			rootJsonObject.put("address",spendTx.getRecipientId());
			rootJsonObject.put("fee",fee.divide(divider));
		}
		
		HashMap<String,Object> result = new HashMap<String, Object>();
		try {
			result =
			        new ObjectMapper().readValue(rootJsonObject.toString(), HashMap.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
		
		@SuppressWarnings("rawtypes")
		TransactionWrapper res = new TransactionWrapper((Map) result);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RawTransaction getRawTransaction(String txId) throws GenericRpcException {
		GenericSignedTx transaction = transactionService.getTransactionByHash(txId).blockingGet();
		
		JSONObject rootJsonObject = new JSONObject();
		rootJsonObject.put("confirmations",getBlockCount() - transaction.getBlockHeight().intValue());
		rootJsonObject.put("txid", txId);
		rootJsonObject.put("blockhash",transaction.getBlockHash());
		List<JSONObject> vOutJsonObjectArray = new ArrayList<JSONObject>();
		
		GenericTx tx = transaction.getTx();
		if (tx instanceof SpendTx) {
			JSONObject vOutJsonObject = new JSONObject();
			List<String> addressesArray = new ArrayList<String>();
			SpendTx spendTx = (SpendTx)tx;
			BigDecimal amount = new BigDecimal(spendTx.getAmount());
			vOutJsonObject.put("amount",amount.divide(divider));
			JSONObject scriptPubKeyJsonObject = new JSONObject();
			addressesArray.add(spendTx.getRecipientId());
			scriptPubKeyJsonObject.put("addresses", addressesArray);
			vOutJsonObject.put("scriptPubKey", scriptPubKeyJsonObject);
			vOutJsonObjectArray.add(vOutJsonObject);
			rootJsonObject.put("vout", vOutJsonObjectArray);
			rootJsonObject.put("senderAddress", spendTx.getSenderId());
		}
		
		HashMap<String,Object> result = new HashMap<String, Object>();
		try {
			result =
			        new ObjectMapper().readValue(rootJsonObject.toString(), HashMap.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
		
		@SuppressWarnings("rawtypes")
		RawTransactionImpl res = new RawTransactionImpl((Map) result);
		return res;
	}


	@SuppressWarnings("unchecked")
	public RawTransaction createRawTx(String senderAddress, String destinationAddress, BigDecimal amountToSend, Tx signedTx) throws GenericRpcException, CryptoException {
		JSONObject rootJsonObject = new JSONObject();
		String txid = transactionService.computeTxHash(signedTx.getTx());
		rootJsonObject.put("confirmations", 0);
		rootJsonObject.put("txid", txid);
		
		List<JSONObject> vOutJsonObjectArray = new ArrayList<JSONObject>();
		
		JSONObject vOutJsonObject = new JSONObject();
		List<String> addressesArray = new ArrayList<String>();
		vOutJsonObject.put("amount", amountToSend);
		JSONObject scriptPubKeyJsonObject = new JSONObject();
		addressesArray.add(destinationAddress);
		scriptPubKeyJsonObject.put("addresses", addressesArray);
		vOutJsonObject.put("scriptPubKey", scriptPubKeyJsonObject);
		vOutJsonObjectArray.add(vOutJsonObject);
		rootJsonObject.put("vout", vOutJsonObjectArray);
		rootJsonObject.put("senderAddress", senderAddress);
		HashMap<String,Object> result = new HashMap<String, Object>();
		try {
			result =
			        new ObjectMapper().readValue(rootJsonObject.toString(), HashMap.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
		
		@SuppressWarnings("rawtypes")
		RawTransactionImpl res = new RawTransactionImpl((Map) result);
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	private AbstractTransaction createSpendTx(String senderAddress, String destinationAddress, BigDecimal amountToSend) throws GenericRpcException, CryptoException {
		Account account = accountService.getAccount(senderAddress).blockingGet();
        KeyBlock block = chainService.getCurrentKeyBlock().blockingGet();
        BigDecimal multiplier = new BigDecimal("1000000000000000000");
        BigInteger amount = amountToSend.multiply(multiplier).toBigInteger();
        String payload = "generalbytes tx";
		// tx will be valid for the next ten blocks
		BigInteger ttl = block.getHeight().add(BigInteger.TEN);
		// we need to increase the current account nonce by one
		BigInteger nonce = account.getNonce().add(BigInteger.ONE);
		
		AbstractTransaction<?> spendTxWithCalculatedFee =
				transactionService
		                 .getTransactionFactory()
		                 .createSpendTransaction(
		                		 senderAddress, destinationAddress, amount, payload, ttl, nonce);
		return spendTxWithCalculatedFee;
	}
    
    public BigDecimal calculateTxFee(String senderAddress, String destinationAddress, BigDecimal amount,  RPCClient client) {
    	BigDecimal fee = BigDecimal.ZERO;
    	try {
    		SpendTransaction spendTx = (SpendTransaction) createSpendTx(senderAddress, destinationAddress, amount);
    		transactionService.createUnsignedTransaction(spendTx).blockingGet();
    		BigDecimal returnFee = new BigDecimal(spendTx.getFee());
    		fee = returnFee.divide(divider);
		} catch (GenericRpcException | CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GenericRpcException(e);
		}
    	return fee;
    }
    
	public Tx createAndSignTransactionAeternity(String senderAddress, String destinationAddress, BigDecimal amountToSend) throws GenericRpcException, CryptoException {
		AeternityWallet wallet = AeternityWallet.loadAeternityWallet(senderAddress);
		AbstractTransaction<?> spendTxWithCalculatedFee =
				createSpendTx(senderAddress, destinationAddress, amountToSend);
		UnsignedTx unsignedTx =
				transactionService.createUnsignedTransaction(spendTxWithCalculatedFee).blockingGet();
		
		Tx signedTx = transactionService.signTransaction(unsignedTx, ((AeternityWallet)wallet).getWalletPrivateKey());
		System.out.println("senderAddress: " + senderAddress);
		System.out.println("pubKey: " + ((AeternityWallet)wallet).getCryptoAddress(CryptoCurrency.AE.getCode()));
		System.out.println("privKey: " + ((AeternityWallet)wallet).getWalletPrivateKey());
		return signedTx;
	}

	public String sendSignedTransactionAeternity(Tx tx) throws GenericRpcException {
		PostTxResponse txResponse = transactionService.postTransaction(tx).blockingGet();
		return txResponse.getTxHash();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Block getBlock(String blockHash) throws GenericRpcException {
		Block block = null;
		try {
			Object blockObject = api.getBlock(blockHash);
			JSONObject blockObjectJSON = AeternityRPCClient.convertToJsonObject(blockObject);
			JSONObject rootJsonObject = new JSONObject();
			rootJsonObject.put("confirmations", getBlockCount() - blockObjectJSON.getInt("height"));
			rootJsonObject.put("blockhash", blockObjectJSON.getString("hash"));
			rootJsonObject.put("height", blockObjectJSON.getInt("height"));
			rootJsonObject.put("version", blockObjectJSON.getInt("version"));
			rootJsonObject.put("time", new java.util.Date(blockObjectJSON.getBigInteger("time").longValueExact()));
			HashMap<String,Object> result = new HashMap<String, Object>();
			result = new ObjectMapper().readValue(rootJsonObject.toString(), HashMap.class);
			block = new BlockMapWrapper((Map) result);
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GenericRpcException();
		}
		return block;
	}

	@Override
	public int getBlockCount() throws GenericRpcException {
		int blockCount = 0;
		try {
			Object statusObject = api.getBlockCount();
			JSONObject statusObjectJSON = AeternityRPCClient.convertToJsonObject(statusObject);
			blockCount = statusObjectJSON.getInt("top_block_height");
			
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GenericRpcException();
		}
		return blockCount;
	}

	@Override
	public BigDecimal getBalance(String account) throws GenericRpcException {
		BigDecimal balance =  BigDecimal.ZERO;
		try {
			Object accountBalance = api.getAccountBalance(account);
			JSONObject accountBalanceJSON = AeternityRPCClient.convertToJsonObject(accountBalance);
			balance = new BigDecimal(accountBalanceJSON.getBigInteger("balance"));
			balance = balance.divide(divider);
			
		} catch (HttpStatusIOException e) {
			if (e.getMessage().contains("404")) return BigDecimal.ZERO;
			throw new GenericRpcException();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GenericRpcException();
		}
		return balance;
	}
    
    public static JSONObject convertToJsonObject(Object objectToConvert) throws JsonProcessingException {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(objectToConvert);
		JSONObject jsonObject = new JSONObject(json);
    	return jsonObject;
    }
    
    public static List<JSONObject> convertToJsonObjectArray(Object objectToConvert) throws JsonProcessingException {
    	List<JSONObject> array = new ArrayList<JSONObject>();
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(objectToConvert);
    	JSONArray jsonarray = new JSONArray(json);
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            array.add(jsonobject);
        }
        return array;
    }
    
    
    

    //@Override
	/*
	 * public boolean equals(Object o) { if (this == o) return true; if (o == null
	 * || getClass() != o.getClass()) return false; AeternityRPCClient rpcClient =
	 * (AeternityRPCClient) o; return Objects.equals(rpcURL, rpcClient.rpcURL) &&
	 * Objects.equals(cryptoCurrency, rpcClient.cryptoCurrency); }
	 */

    //@Override
	/*
	 * public int hashCode() { return Objects.hash(rpcURL, cryptoCurrency); }
	 */
    

    private static class MapWrapper {

    	  @SuppressWarnings("rawtypes")
    	  public final Map m;

    	  @SuppressWarnings("rawtypes")
    	  public MapWrapper(Map m) {
    	    this.m = m;
    	  }

    	  @SuppressWarnings("unused")
    	  public Boolean mapBool(String key) {
    	    return mapBool(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public Integer mapInt(String key) {
    	    return mapInt(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public Long mapLong(String key) {
    	    return mapLong(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public String mapStr(String key) {
    		  return mapStr(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public Date mapCTime(String key) {
    	    return mapCTime(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public BigDecimal mapBigDecimal(String key) {
    	    return mapBigDecimal(m, key);
    	  }

    	  @SuppressWarnings("unused")
    	  public byte[] mapHex(String key) {
    	    return mapHex(m, key);
    	  }

    	  public static Boolean mapBool(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object val = m.get(key);
    	    return val instanceof Boolean ? (Boolean) val : Boolean.FALSE;
    	  }

    	  public static BigDecimal mapBigDecimal(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object val = m.get(key);
    	    return val instanceof BigDecimal ? (BigDecimal) val : new BigDecimal((String) val);
    	  }

    	  public static Integer mapInt(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object val = m.get(key);
    	    return val instanceof Number ? ((Number) val).intValue() : null;
    	  }

    	  public static Long mapLong(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object val = m.get(key);
    	    return val instanceof Number ? ((Number) val).longValue() : null;
    	  }

    	  public static String mapStr(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object v = m.get(key);
    	    return v == null ? null : String.valueOf(v);
    	  }

    	  public static Date mapCTime(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object v = m.get(key);
    	    return v == null ? null : new Date(mapLong(m, key) * 1000);
    	  }

    	  public static byte[] mapHex(@SuppressWarnings("rawtypes") Map m, String key) {
    	    Object v = m.get(key);
    	    return v == null ? null : HexCoder.decode((String) v);
    	  }

    	  @Override
    	  public String toString() {
    	    return String.valueOf(m);
    	  }

    	}
    
    @SuppressWarnings("serial")
    private class TransactionWrapper extends MapWrapper implements Transaction, Serializable {

      @SuppressWarnings("rawtypes")
      public TransactionWrapper(Map m) {
        super(m);
      }

      @Override
      public String account() {
        return mapStr(m, "account");
      }

      @Override
      public String address() {
        return mapStr(m, "address");
      }

      @Override
      public String category() {
        return mapStr(m, "category");
      }

      @Override
      public BigDecimal amount() {
        return mapBigDecimal(m, "amount");
      }

      @Override
      public BigDecimal fee() {
        return mapBigDecimal(m, "fee");
      }

      @Override
      public int confirmations() {
        return mapInt(m, "confirmations");
      }

      @Override
      public String blockHash() {
        return mapStr(m, "blockhash");
      }

      @Override
      public int blockIndex() {
        return mapInt(m, "blockindex");
      }

      @Override
      public Date blockTime() {
        return mapCTime(m, "blocktime");
      }

      @Override
      public String txId() {
        return mapStr(m, "txid");
      }

      @Override
      public Date time() {
        return mapCTime(m, "time");
      }

      @Override
      public Date timeReceived() {
        return mapCTime(m, "timereceived");
      }

      @Override
      public String comment() {
        return mapStr(m, "comment");
      }

      @Override
      public String commentTo() {
        return mapStr(m, "to");
      }

      @Override
      public boolean generated() {
        return mapBool(m, "generated");
      }

      private RawTransaction raw = null;

      @Override
      public RawTransaction raw() {
        if (raw == null)
          try {
            raw = getRawTransaction(txId());
          } catch (GenericRpcException ex) {
            //logger.warning(ex.getMessage());
          }
        return raw;
      }

      @Override
      public String toString() {
        return m.toString();
      }
    }
    
    @SuppressWarnings("serial")
	private class BlockMapWrapper extends MapWrapper implements Block, Serializable {

        @SuppressWarnings("rawtypes")
		public BlockMapWrapper(Map m) {
          super(m);
        }

        @Override
        public String hash() {
          return mapStr("hash");
        }

        @Override
        public int confirmations() {
          return mapInt("confirmations");
        }

        @Override
        public int size() {
          return mapInt("size");
        }

        @Override
        public int height() {
          return mapInt("height");
        }

        @Override
        public int version() {
          return mapInt("version");
        }

        @Override
        public String merkleRoot() {
          return mapStr("merkleroot");
        }

        @Override
        public String chainwork() {
          return mapStr("chainwork");
        }

        @SuppressWarnings("unchecked")
		@Override
        public List<String> tx() {
          return (List<String>) m.get("tx");
        }

        @Override
        public Date time() {
          return mapCTime("time");
        }

        @Override
        public long nonce() {
          return mapLong("nonce");
        }

        @Override
        public String bits() {
          return mapStr("bits");
        }

        @Override
        public BigDecimal difficulty() {
          return mapBigDecimal("difficulty");
        }

        @Override
        public String previousHash() {
          return mapStr("previousblockhash");
        }

        @Override
        public String nextHash() {
          return mapStr("nextblockhash");
        }

        @Override
        public Block previous() throws GenericRpcException {
          if (!m.containsKey("previousblockhash"))
            return null;
          return getBlock(previousHash());
        }

        @Override
        public Block next() throws GenericRpcException {
          if (!m.containsKey("nextblockhash"))
            return null;
          return getBlock(nextHash());
        }

      }
    
    @SuppressWarnings("serial")
	private class NetworkInfoWrapper extends MapWrapper implements NetworkInfo, Serializable {

        @SuppressWarnings("rawtypes")
		public NetworkInfoWrapper(Map m) {
          super(m);
        }

        @Override
        public long version() {
          return mapLong("version");
        }

        @Override
        public String subversion() {
          return mapStr("subversion");
        }

        @Override
        public long protocolVersion() {
          return mapLong("protocolversion");
        }

        @Override
        public String localServices() {
          return mapStr("localservices");
        }

        @Override
        public boolean localRelay() {
          return mapBool("localrelay");
        }

        @Override
        public long timeOffset() {
          return mapLong("timeoffset");
        }

        @Override
        public long connections() {
          return mapLong("connections");
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
        public List<Network> networks() {
          List<Map> maps = (List<Map>) m.get("networks");
          List<Network> networks = new LinkedList<Network>();
          for (Map m : maps) {
            Network net = new NetworkWrapper(m);
            networks.add(net);
          }
          return networks;
        }

        @Override
        public BigDecimal relayFee() {
          return mapBigDecimal("relayfee");
        }

        @SuppressWarnings("unchecked")
		@Override
        public List<String> localAddresses() {
          return (List<String>) m.get("localaddresses");
        }

        @Override
        public String warnings() {
          return mapStr("warnings");
        }
      }
    
    @SuppressWarnings("serial")
	private class NetworkWrapper extends MapWrapper implements Network, Serializable {

        @SuppressWarnings("rawtypes")
		public NetworkWrapper(Map m) {
          super(m);
        }

        @Override
        public String name() {
          return mapStr("name");
        }

        @Override
        public boolean limited() {
          return mapBool("limited");
        }

        @Override
        public boolean reachable() {
          return mapBool("reachable");
        }

        @Override
        public String proxy() {
          return mapStr("proxy");
        }

        @Override
        public boolean proxyRandomizeCredentials() {
          return mapBool("proxy_randomize_credentials");
        }
      }
    
    @SuppressWarnings("serial")
	public class RawTransactionImpl extends MapWrapper implements RawTransaction, Serializable {

        public RawTransactionImpl(Map<String, Object> tx) {
          super(tx);
        }

        @Override
        public String hex() {
          return mapStr("hex");
        }

        @Override
        public String txId() {
          return mapStr("txid");
        }

        public String senderAddress() {
          return mapStr("senderAddress");
        }
        
        @Override
        public int version() {
          return mapInt("version");
        }

        @Override
        public long lockTime() {
          return mapLong("locktime");
        }

        @Override
        public String hash() {
          return mapStr("hash");
        }

        @Override
        public long size() {
          return mapLong("size");
        }

        @Override
        public long vsize() {
          return mapLong("vsize");
        }

        private class InImpl extends MapWrapper implements In, Serializable {

          @SuppressWarnings("rawtypes")
          public InImpl(Map m) {
            super(m);
          }

          @Override
          public String txid() {
            return mapStr("txid");
          }

          @Override
          public Integer vout() {
            return mapInt("vout");
          }

          @SuppressWarnings({ "unchecked", "rawtypes" })
          @Override
          public Map<String, Object> scriptSig() {
            return (Map) m.get("scriptSig");
          }

          @Override
          public long sequence() {
            return mapLong("sequence");
          }

          @Override
          public RawTransaction getTransaction() {
            try {
              return getRawTransaction(mapStr("txid"));
            } catch (GenericRpcException ex) {
              throw new RuntimeException(ex);
            }
          }

          @Override
          public Out getTransactionOutput() {
            return getTransaction().vOut().get(mapInt("vout"));
          }

          @Override
          public String scriptPubKey() {
            return mapStr("scriptPubKey");
          }

          @Override
          public String address() {
              return mapStr("address");
          }
        }

        @SuppressWarnings("unchecked")
		@Override
        public List<In> vIn() {
          final List<Map<String, Object>> vIn = (List<Map<String, Object>>) m.get("vin");
          return new AbstractList<In>() {

            @Override
            public In get(int index) {
              return new InImpl(vIn.get(index));
            }

            @Override
            public int size() {
              return vIn.size();
            }
          };
        }

        private class OutImpl extends MapWrapper implements Out, Serializable {

          @SuppressWarnings("rawtypes")
          public OutImpl(Map m) {
            super(m);
          }

          @Override
          public BigDecimal value() {
            return mapBigDecimal("value");
          }

          @Override
          public int n() {
            return mapInt("n");
          }

          private class ScriptPubKeyImpl extends MapWrapper implements ScriptPubKey, Serializable {

            @SuppressWarnings("rawtypes")
			public ScriptPubKeyImpl(Map m) {
              super(m);
            }

            @Override
            public String asm() {
              return mapStr("asm");
            }

            @Override
            public String hex() {
              return mapStr("hex");
            }

            @Override
            public int reqSigs() {
              return mapInt("reqSigs");
            }

            @Override
            public String type() {
              return mapStr("type");
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public List<String> addresses() {
              return (List) m.get("addresses");
            }

          }

          @SuppressWarnings("rawtypes")
          @Override
          public ScriptPubKey scriptPubKey() {
            return new ScriptPubKeyImpl((Map) m.get("scriptPubKey"));
          }

          @Override
          public TxInput toInput() {
            return new BasicTxInput(transaction().txId(), n());
          }

          @Override
          public RawTransaction transaction() {
            return RawTransactionImpl.this;
          }

        }

        @SuppressWarnings("unchecked")
		@Override
        public List<Out> vOut() {
          final List<Map<String, Object>> vOut = (List<Map<String, Object>>) m.get("vout");
          return new AbstractList<Out>() {

            @Override
            public Out get(int index) {
              return new OutImpl(vOut.get(index));
            }

            @Override
            public int size() {
              return vOut.size();
            }
          };
        }

        @Override
        public String blockHash() {
          return mapStr("blockhash");
        }

        @Override
        public Integer confirmations() {
          Object o = m.get("confirmations");
          return o == null ? null : ((Number)o).intValue();
        }

        @Override
        public Date time() {
          return mapCTime("time");
        }

        @Override
        public Date blocktime() {
          return mapCTime("blocktime");
        }

        @Override
        public long height()
        {
            return mapLong("height");
        }

      }

      
	
}
