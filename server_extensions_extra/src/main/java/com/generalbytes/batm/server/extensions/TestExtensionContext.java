package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.exceptions.BuyException;
import com.generalbytes.batm.server.extensions.exceptions.CashbackException;
import com.generalbytes.batm.server.extensions.exceptions.SellException;
import com.generalbytes.batm.server.extensions.exceptions.UpdateException;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestExtensionContext implements IExtensionContext {
    @Override
    public void addTransactionListener(ITransactionListener listener) {

    }

    @Override
    public boolean removeTransactionListener(ITransactionListener listener) {
        return false;
    }

    @Override
    public void addTerminalListener(ITerminalListener listener) {

    }

    @Override
    public void removeTerminalListener(ITerminalListener listener) {

    }

    @Override
    public ITransactionDetails findTransactionByTransactionId(String remoteOrLocalTransactionId) {
        return null;
    }

    @Override
    public ITransactionDetails findTransactionByTransactionUUIDAndType(String uuid, int type) {
        return null;
    }

    @Override
    public List<ITransactionDetails> findAllTransactionsByIdentityId(String publicIdentityId) {
        return null;
    }

    @Override
    public List<ITransactionDetails> findTransactions(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String previousRID, boolean includeBanknotes) {
        return null;
    }
    @Override
    public ITransactionDetails updateTransaction(String rid, int status, String detail) throws UpdateException {
        return null;
    }

    @Override
    public IPerson findPersonByChatId(String chatUserId) {
        return null;
    }

    @Override
    public boolean hasPersonPermissionToObject(int permissionLevel, IPerson person, Object obj) {
        return false;
    }

    @Override
    public boolean isTerminalFromSameOrganizationAsPerson(String terminalSerialNumber, IPerson person) {
        return false;
    }

    @Override
    public IIdentity findIdentityByIdentityId(String publicIdentityId) {
        return null;
    }

    @Override
    public List<IIdentity> findIdentitiesByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public List<IIdentity> findAllIdentitiesByState(int state) {
        return null;
    }

    @Override
    public List<IIdentity> findIdentityByPhoneNumber(String phoneNumber, String countryName) {
        return null;
    }

    @Override
    public List<IIdentity> findIdentitiesByDocumentNumber(String documentNumber) {
        return null;
    }

    @Override
    public IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered) {
        return null;
    }

    @Override
    public IIdentity addIdentity(String configurationCashCurrency, String terminalSerialNumber, String externalId, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, String note, int state, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, Date created, Date registered, String language) {
        return null;
    }

    @Override
    public boolean addIdentityPiece(String identityPublicId, IIdentityPiece iidentityPiece) {
        return false;
    }

    @Override
    public IIdentity updateIdentity(String identityId, String externalId, int state, int type, Date created, Date registered, BigDecimal vipBuyDiscount, BigDecimal vipSellDiscount, String note, List<ILimit> limitCashPerTransaction, List<ILimit> limitCashPerHour, List<ILimit> limitCashPerDay, List<ILimit> limitCashPerWeek, List<ILimit> limitCashPerMonth, List<ILimit> limitCashPer3Months, List<ILimit> limitCashPer12Months, List<ILimit> limitCashPerCalendarQuarter, List<ILimit> limitCashPerCalendarYear, List<ILimit> limitCashTotalIdentity, String configurationCashCurrency) {
        return null;
    }

    @Override
    public ITunnelManager getTunnelManager() {
        return (walletLogin, tunnelPassword, originalWalletAddress) -> originalWalletAddress;
    }

    @Override
    public void sendMailAsync(String from, String addressListTo, String subject, String messageText, String replyTo) {

    }

    @Override
    public void sendMailAsyncWithAttachment(String from, String addresslistTo, String subject, String messageText, String attachmentFileName, byte[] attachmentContent, String attachmentMimeType, String replyTo) {

    }

    @Override
    public void sendHTMLMailAsync(String from, String addresslistTo, String subject, String messageText, String replyTo, EmbeddedEmailImage... embeddedEmailImages) {

    }

    @Override
    public void sendHTMLMailAsyncWithAttachment(String from, String addresslistTo, String subject, String messageText, String attachmentFileName, byte[] attachmentContent, String attachmentMimeType, String replyTo) {

    }

    @Override
    public void sendSMSAsync(String terminalSN, String phonenumber, String messageText) {

    }

    @Override
    public void addTask(String name, ITask tt, Runnable onFinish) {

    }

    @Override
    public String getServerVersion() {
        return null;
    }

    @Override
    public BigDecimal calculateCashAvailableForSell(String terminalSerialNumber, String fiatCurrency) {
        return null;
    }

    @Override
    public Map<BigDecimal, Integer> getAvailableBanknotesConsideringFutureWithdrawals(String terminalSerialNumber, String fiatCurrency) {
        return null;
    }

    @Override
    public ITransactionSellInfo sellCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String identityPublicId, String discountCode) throws SellException {
        return null;
    }

    @Override
    public ITransactionBuyInfo buyCrypto(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, BigDecimal cryptoAmount, String cryptoCurrency, String destinationAddress, String identityPublicId, String discountCode) throws BuyException {
        return null;
    }

    @Override
    public ITransactionCashbackInfo cashback(String terminalSerialNumber, BigDecimal fiatAmount, String fiatCurrency, String identityPublicId) throws CashbackException {
        return null;
    }

    @Override
    public Map<Integer, List<IExchangeRateInfo>> getExchangeRateInfo(String terminalSerialNumber, int directions) {
        return null;
    }

    @Override
    public Map<String, IAmountWithDiscount> calculateCryptoAmounts(String terminalSerialNumber, List<String> cryptoCurrencies, BigDecimal cashAmount, String cashCurrency, int direction, String discountCode, String identityPublicId) {
        return null;
    }

    @Override
    public List<IBanknoteCounts> getCashBoxes(String terminalSerialNumber) {
        return null;
    }

    @Override
    public List<ITerminal> findAllTerminals() {
        return null;
    }

    @Override
    public ITerminal findTerminalBySerialNumber(String serialNumber) {
        return null;
    }

    @Override
    public List<String> findTerminalsWithAvailableCashForSell(BigDecimal fiatAmount, String fiatCurrency, List<String> listOfTerminalSerialNumbers) {
        return null;
    }

    @Override
    public byte[] createPaperWallet7ZIP(String privateKey, String address, String password, String cryptoCurrency) {
        return new byte[0];
    }

    @Override
    public SimpleDateFormat getTimeFormatByPerson(IPerson person) {
        return null;
    }

    @Override
    public IWallet findBuyWallet(String terminalSerialNumber, String cryptoCurrency) {
        return null;
    }

    @Override
    public List<ICryptoConfiguration> findCryptoConfigurationsByTerminalSerialNumbers(List<String> serialNumbers) {
        return null;
    }

    @Override
    public WatchListResult searchWatchList(WatchListQuery query) {
        return null;
    }

    @Override
    public PhoneNumberQueryResult queryPhoneNumber(String phoneNumber, String terminalSerialNumber) {
        return null;
    }

    @Override
    public List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date terminalTimeFrom, Date terminalTimeTo) {
        return null;
    }

    @Override
    public List<ITerminalCashCollectionRecord> getCashCollections(String terminalSerialNumber, Date serverTimeFrom, Date serverTimeTo, String publicIdFrom) {
        return null;
    }

    @Override
    public List<IEventRecord> getEvents(String terminalSerialNumber, Date dateFrom, Date dateTo) {
        return null;
    }

    @Override
    public List<IRemainingLimit> getIdentityRemainingLimits(String fiatCurrency, String terminalSerialNumber, String identityPublicId) {
        return null;
    }

    @Override
    public IApiAccess getAPIAccessByKey(String apiKey) {
        return null;
    }

    @Override
    public IDiscount createDiscount(IOrganization organization, DiscountSpec discountSpec) {
        return null;
    }

    @Override
    public List<IOrganization> getOrganizations() {
        return Collections.emptyList();
    }

    @Override
    public boolean triggerCollectPhoto(List<String> terminalSerialNumbers) {
        return false;
    }
}
