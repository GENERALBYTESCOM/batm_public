package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponsePrepareTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOTransactionSignature;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.libdohj.params.DogecoinMainNetParams;
import org.libdohj.params.DogecoinTestNet3Params;
import org.libdohj.params.LitecoinMainNetParams;
import org.libdohj.params.LitecoinTestNet3Params;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service to client-side signing of transactions.
 * <p>
 * Before creating and signing of transaction is necessary to call "Prepare Transaction" API to get {@link BlockIOResponsePrepareTransaction},
 * then received data are used to create signed {@link BlockIOTransaction}
 * </p><p>
 * Implementation based on Block.io <a href="https://github.com/BlockIo/block_io-java/blob/master/src/main/java/lib/blockIo/BlockIo.java">library</a>
 * </p>
 */
public class BlockIOSignService {

    private final String pin;

    public BlockIOSignService(String pin) {
        this.pin = pin;
    }

    public BlockIOTransaction createAndSignTransaction(BlockIOResponsePrepareTransaction response) throws Exception {
        String status = response.getStatus();
        BlockIOResponsePrepareTransaction.BlockIOData data = response.getData();
        String networkString = data.getNetwork();

        NetworkParameters networkParams = null;
        if (status != null && status.equals("success") && networkString != null) {
            networkParams = getNetwork(networkString);
        }

        List<BlockIOResponsePrepareTransaction.BlockIOInput> inputs = data.getInputs();
        List<BlockIOResponsePrepareTransaction.BlockIOOutput> outputs = data.getOutputs();
        List<BlockIOResponsePrepareTransaction.BlockIOInputAddressData> inputAddressData = data.getInput_address_data();

        Transaction tx = new Transaction(networkParams);

        for (BlockIOResponsePrepareTransaction.BlockIOInput input : inputs) {
            Sha256Hash previousTxId = Sha256Hash.wrap(input.getPrevious_txid());
            int outputIndex = input.getPrevious_output_index();
            tx.addInput(previousTxId, outputIndex, ScriptBuilder.createEmpty()).clearScriptBytes();
        }

        for (BlockIOResponsePrepareTransaction.BlockIOOutput output : outputs) {
            Address receivingAddress = Address.fromString(networkParams, output.getReceiving_address());
            Coin outputValue = Coin.parseCoin(output.getOutput_value().toString());
            tx.addOutput(outputValue, receivingAddress);
        }

        String txHex = txToHexString(tx);

        HashMap<String, BlockIOResponsePrepareTransaction.BlockIOInputAddressData> addressDataMap = new HashMap<>();
        HashMap<String, Script> addressScriptMap = new HashMap<>();

        for (BlockIOResponsePrepareTransaction.BlockIOInputAddressData addressData : inputAddressData) {
            String addressType = addressData.getAddress_type();
            int requiredSignatures = addressData.getRequired_signatures();
            List<String> publicKeys = addressData.getPublic_keys();

            addressDataMap.put(addressData.getAddress(), addressData);

            ArrayList<ECKey> ecKeys = new ArrayList<>();
            for (String publicKey : publicKeys) {
                ECKey ecKey = ECKey.fromPublicOnly(Hex.decode(publicKey));
                ecKeys.add(ecKey);
            }

            Script redeem;
            if (addressType.equals("P2WSH-over-P2SH") || addressType.equals("WITNESS_V0") || addressType.equals("P2SH")) {
                redeem = ScriptBuilder.createMultiSigOutputScript(requiredSignatures, ecKeys);
            } else if (addressType.equals("P2PKH") || addressType.equals("P2WPKH") || addressType.equals("P2WPKH-over-P2SH")) {
                redeem = ScriptBuilder.createP2PKHOutputScript(ecKeys.get(0));
            } else {
                throw new Exception("Unrecognized address type: " + addressType);
            }
            addressScriptMap.put(addressData.getAddress(), redeem);
        }

        HashMap<String, ECKey> userKeys = new HashMap<>();
        BlockIOResponsePrepareTransaction.BlockIOUserKey userKey = data.getUser_key();
        if (userKey != null) {
            // we don't have the key to sign for transaction yet
            if (pin != null) {
                // use the user_key to extract private key dynamically
                String publicKey = userKey.getPublic_key();
                ECKey key = BlockIOKey.dynamicExtractKey(userKey, pin);

                if (!key.getPublicKeyAsHex().equals(publicKey)) {
                    throw new Exception("Fail: Invalid Secret PIN provided.");
                }
                // we have the key, let's save it for later use
                userKeys.put(publicKey, key);
            } else {
                throw new Exception("Fail: No PIN provided to decrypt private key.");
            }
        }
        String expectedUnsignedTxId = data.getExpected_unsigned_txid();
        if (expectedUnsignedTxId != null && !expectedUnsignedTxId.equals(tx.getTxId().toString())) {
            throw new Exception("Expected unsigned transaction ID mismatch. Please report this error to support@block.io.");
        }

        boolean isTxFullySigned = true;
        List<BlockIOTransactionSignature> signatures = new ArrayList<>();

        for (BlockIOResponsePrepareTransaction.BlockIOInput input : inputs) {

            String spendingAddress = input.getSpending_address();
            Script addressScript = addressScriptMap.get(spendingAddress);
            BlockIOResponsePrepareTransaction.BlockIOInputAddressData addressData = addressDataMap.get(spendingAddress);
            String spendingAddressType = addressData.getAddress_type();
            int addressDataRequiredSignatures = addressData.getRequired_signatures();
            List<String> publicKeys = addressData.getPublic_keys();
            int currentSignaturesCount = 0;
            int inputIndex = input.getInput_index();
            Coin inputValue = Coin.parseCoin(input.getInput_value().toString());
            Sha256Hash signatureHash;
            if (spendingAddressType.equals("P2WSH-over-P2SH")
                || spendingAddressType.equals("WITNESS_V0")
                || spendingAddressType.equals("P2WPKH")
                || spendingAddressType.equals("P2WPKH-over-P2SH")
            ) {
                signatureHash = tx.hashForWitnessSignature(inputIndex, addressScript, inputValue, Transaction.SigHash.ALL, false);
            } else {
                signatureHash = tx.hashForSignature(inputIndex, addressScript, Transaction.SigHash.ALL, false);
            }

            ArrayList<TransactionSignature> transactionSignatures = new ArrayList<>();
            ArrayList<ECKey> ecKeys = new ArrayList<>();
            for (String publicKey : publicKeys) {
                if (userKeys.containsKey(publicKey)) {
                    ECKey ecKey = userKeys.get(publicKey);
                    ECKey.ECDSASignature ecdsaSignature = ecKey.sign(signatureHash);
                    TransactionSignature transactionSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, false);
                    transactionSignatures.add(transactionSignature);
                    ecKeys.add(ecKey);

                    BlockIOTransactionSignature signature = new BlockIOTransactionSignature();
                    signature.setInput_index(inputIndex);
                    signature.setPublic_key(publicKey);
                    signature.setSignature(Hex.toHexString(transactionSignature.encodeToDER()));

                    signatures.add(signature);
                    currentSignaturesCount++;
                }
            }

            if (spendingAddressType.equals("P2WSH-over-P2SH")) {
                tx.getInput(inputIndex).setScriptSig(createBlockIoP2WSHScript(addressScript));
                tx.getInput(inputIndex).setWitness(redeemP2WSH(transactionSignatures, addressScript));
            } else if (spendingAddressType.equals("WITNESS_V0")) {
                tx.getInput(inputIndex).setWitness(redeemP2WSH(transactionSignatures, addressScript));
            } else if (spendingAddressType.equals("P2WPKH")) {
                tx.getInput(inputIndex).setWitness(TransactionWitness.redeemP2WPKH(transactionSignatures.get(0), ecKeys.get(0)));
            } else if (spendingAddressType.equals("P2WPKH-over-P2SH")) {
                Script redeem = ScriptBuilder.createP2WPKHOutputScript(ecKeys.get(0));
                tx.getInput(inputIndex).setScriptSig(createBlockIoP2WPKHScript(redeem));
                tx.getInput(inputIndex).setWitness(TransactionWitness.redeemP2WPKH(transactionSignatures.get(0), ecKeys.get(0)));
            } else if (spendingAddressType.equals("P2SH")) {
                Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(transactionSignatures, addressScript);
                tx.getInput(inputIndex).setScriptSig(inputScript);
            } else {
                // P2PKH
                Script inputScript = ScriptBuilder.createInputScript(transactionSignatures.get(0), ecKeys.get(0));
                tx.getInput(inputIndex).setScriptSig(inputScript);
            }

            if (currentSignaturesCount < addressDataRequiredSignatures) {
                isTxFullySigned = false;
            }
        }

        if (isTxFullySigned) {
            txHex = txToHexString(tx);
            signatures = null;
        }

        BlockIOTransaction blockIOTransaction = new BlockIOTransaction();
        blockIOTransaction.setTx_type(data.getTx_type());
        blockIOTransaction.setTx_hex(txHex);
        blockIOTransaction.setSignatures(signatures);

        return blockIOTransaction;
    }

    private String txToHexString(Transaction tx) {
        return Hex.toHexString(tx.unsafeBitcoinSerialize());
    }

    private Script createBlockIoP2WSHScript(Script redeem) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write((byte) 0);
        output.write((byte) 32);
        output.write(Sha256Hash.hash(redeem.getProgram()));
        byte[] out = output.toByteArray();
        return new ScriptBuilder().data(out).build();
    }

    private Script createBlockIoP2WPKHScript(Script redeem) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write((byte) 0);
        output.write((byte) 20);
        output.write(redeem.getPubKeyHash());
        byte[] out = output.toByteArray();
        return new ScriptBuilder().data(out).build();
    }

    private TransactionWitness redeemP2WSH(List<TransactionSignature> signatures, Script redeem) {
        int witnesses = signatures.size() + 2;
        TransactionWitness wit = new TransactionWitness(witnesses);
        wit.setPush(0, new byte[0]);
        int witnessIte = 1;
        for (TransactionSignature signature : signatures) {
            wit.setPush(witnessIte, signature.encodeToBitcoin());
            witnessIte++;
        }
        wit.setPush(witnessIte, redeem.getProgram());
        return wit;
    }

    private NetworkParameters getNetwork(String networkString) {
        switch (networkString) {
            case "LTC":
                return LitecoinMainNetParams.get();
            case "DOGE":
                return DogecoinMainNetParams.get();
            case "BTCTEST":
                return NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
            case "LTCTEST":
                return LitecoinTestNet3Params.get();
            case "DOGETEST":
                return DogecoinTestNet3Params.get();
            default:
                return NetworkParameters.fromID(NetworkParameters.ID_MAINNET);
        }
    }
}
