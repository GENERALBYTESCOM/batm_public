package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

import java.math.BigDecimal;
import java.util.List;

public class BlockIOResponsePrepareTransaction {
    private String status;
    private BlockIOData data;

    public static class BlockIOData {
        private String network;
        private String tx_type;
        private String estimated_tx_size;
        private String expected_unsigned_txid;
        private List<BlockIOInput> inputs;
        private List<BlockIOOutput> outputs;
        private List<BlockIOInputAddressData> input_address_data;
        private BlockIOUserKey user_key;

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getTx_type() {
            return tx_type;
        }

        public void setTx_type(String tx_type) {
            this.tx_type = tx_type;
        }

        public String getEstimated_tx_size() {
            return estimated_tx_size;
        }

        public void setEstimated_tx_size(String estimated_tx_size) {
            this.estimated_tx_size = estimated_tx_size;
        }

        public String getExpected_unsigned_txid() {
            return expected_unsigned_txid;
        }

        public void setExpected_unsigned_txid(String expected_unsigned_txid) {
            this.expected_unsigned_txid = expected_unsigned_txid;
        }

        public List<BlockIOInput> getInputs() {
            return inputs;
        }

        public void setInputs(List<BlockIOInput> inputs) {
            this.inputs = inputs;
        }

        public List<BlockIOOutput> getOutputs() {
            return outputs;
        }

        public void setOutputs(List<BlockIOOutput> outputs) {
            this.outputs = outputs;
        }

        public List<BlockIOInputAddressData> getInput_address_data() {
            return input_address_data;
        }

        public void setInput_address_data(List<BlockIOInputAddressData> input_address_data) {
            this.input_address_data = input_address_data;
        }

        public BlockIOUserKey getUser_key() {
            return user_key;
        }

        public void setUser_key(BlockIOUserKey user_key) {
            this.user_key = user_key;
        }
    }

    public static class BlockIOInput {
        private Integer input_index;
        private Integer previous_output_index;
        private String previous_txid;
        private BigDecimal input_value;
        private String spending_address;

        public Integer getInput_index() {
            return input_index;
        }

        public void setInput_index(Integer input_index) {
            this.input_index = input_index;
        }

        public Integer getPrevious_output_index() {
            return previous_output_index;
        }

        public void setPrevious_output_index(Integer previous_output_index) {
            this.previous_output_index = previous_output_index;
        }

        public String getPrevious_txid() {
            return previous_txid;
        }

        public void setPrevious_txid(String previous_txid) {
            this.previous_txid = previous_txid;
        }

        public BigDecimal getInput_value() {
            return input_value;
        }

        public void setInput_value(BigDecimal input_value) {
            this.input_value = input_value;
        }

        public String getSpending_address() {
            return spending_address;
        }

        public void setSpending_address(String spending_address) {
            this.spending_address = spending_address;
        }
    }

    public static class BlockIOOutput {
        private Integer output_index;
        private String output_category;
        private BigDecimal output_value;
        private String receiving_address;

        public Integer getOutput_index() {
            return output_index;
        }

        public void setOutput_index(Integer output_index) {
            this.output_index = output_index;
        }

        public String getOutput_category() {
            return output_category;
        }

        public void setOutput_category(String output_category) {
            this.output_category = output_category;
        }

        public BigDecimal getOutput_value() {
            return output_value;
        }

        public void setOutput_value(BigDecimal output_value) {
            this.output_value = output_value;
        }

        public String getReceiving_address() {
            return receiving_address;
        }

        public void setReceiving_address(String receiving_address) {
            this.receiving_address = receiving_address;
        }
    }

    public static class BlockIOInputAddressData {
        private Integer required_signatures;
        private String address;
        private String address_type;
        private List<String> public_keys;

        public Integer getRequired_signatures() {
            return required_signatures;
        }

        public void setRequired_signatures(Integer required_signatures) {
            this.required_signatures = required_signatures;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress_type() {
            return address_type;
        }

        public void setAddress_type(String address_type) {
            this.address_type = address_type;
        }

        public List<String> getPublic_keys() {
            return public_keys;
        }

        public void setPublic_keys(List<String> public_keys) {
            this.public_keys = public_keys;
        }
    }

    public static class BlockIOUserKey {
        private String public_key;
        private String encrypted_passphrase;
        private BlockIOUserKeyAlgorithm algorithm;

        public String getPublic_key() {
            return public_key;
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }

        public String getEncrypted_passphrase() {
            return encrypted_passphrase;
        }

        public void setEncrypted_passphrase(String encrypted_passphrase) {
            this.encrypted_passphrase = encrypted_passphrase;
        }

        public BlockIOUserKeyAlgorithm getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(BlockIOUserKeyAlgorithm algorithm) {
            this.algorithm = algorithm;
        }
    }

    public static class BlockIOUserKeyAlgorithm {
        private String pbkdf2_salt;
        private Integer pbkdf2_iterations;
        private String pbkdf2_hash_function;
        private Integer pbkdf2_phase1_key_length;
        private Integer pbkdf2_phase2_key_length;
        private String aes_iv;
        private String aes_cipher;
        private String aes_auth_tag;
        private String aes_auth_data;

        public String getPbkdf2_salt() {
            return pbkdf2_salt;
        }

        public void setPbkdf2_salt(String pbkdf2_salt) {
            this.pbkdf2_salt = pbkdf2_salt;
        }

        public Integer getPbkdf2_iterations() {
            return pbkdf2_iterations;
        }

        public void setPbkdf2_iterations(Integer pbkdf2_iterations) {
            this.pbkdf2_iterations = pbkdf2_iterations;
        }

        public String getPbkdf2_hash_function() {
            return pbkdf2_hash_function;
        }

        public void setPbkdf2_hash_function(String pbkdf2_hash_function) {
            this.pbkdf2_hash_function = pbkdf2_hash_function;
        }

        public Integer getPbkdf2_phase1_key_length() {
            return pbkdf2_phase1_key_length;
        }

        public void setPbkdf2_phase1_key_length(Integer pbkdf2_phase1_key_length) {
            this.pbkdf2_phase1_key_length = pbkdf2_phase1_key_length;
        }

        public Integer getPbkdf2_phase2_key_length() {
            return pbkdf2_phase2_key_length;
        }

        public void setPbkdf2_phase2_key_length(Integer pbkdf2_phase2_key_length) {
            this.pbkdf2_phase2_key_length = pbkdf2_phase2_key_length;
        }

        public String getAes_iv() {
            return aes_iv;
        }

        public void setAes_iv(String aes_iv) {
            this.aes_iv = aes_iv;
        }

        public String getAes_cipher() {
            return aes_cipher;
        }

        public void setAes_cipher(String aes_cipher) {
            this.aes_cipher = aes_cipher;
        }

        public String getAes_auth_tag() {
            return aes_auth_tag;
        }

        public void setAes_auth_tag(String aes_auth_tag) {
            this.aes_auth_tag = aes_auth_tag;
        }

        public String getAes_auth_data() {
            return aes_auth_data;
        }

        public void setAes_auth_data(String aes_auth_data) {
            this.aes_auth_data = aes_auth_data;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BlockIOData getData() {
        return data;
    }

    public void setData(BlockIOData data) {
        this.data = data;
    }
}
