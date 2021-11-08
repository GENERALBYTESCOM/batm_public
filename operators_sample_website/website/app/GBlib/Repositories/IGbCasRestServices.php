<?php

namespace App\GBlib\Repositories;

interface IGbCasRestServices
{

    const CAS_URL = 'https://127.0.0.1:7743'; // your Crypto application server url
    const API_KEY = 'PM2TB2IMBVIREBI3C3TIPBUK7CB2BOVAS'; // your Morphis - CAS Api Key

    /*
     * Path to your RestServices placed on
     * "https://github.com/GENERALBYTESCOM/batm_public/tree/master/operators_sample_website/src/main"
     */
    const TERMINALS = self::CAS_URL . "/extensions/website/terminals";
    const TERMINALS_WITH_AVAILABLE_CASH = self::CAS_URL . "/extensions/website/terminals-with-available-cash";
    const SELL_CRYPTO = self::CAS_URL . "/extensions/website/sell-crypto";
    const STATUS = self::CAS_URL . "/extensions/website/status";

    /*
     * Set your fiat currency banknote denomination
     */
    const SWITCH_BANKNOTE_DENOMINATION_BY_FIAT = [
        'USD' => 10,
        'CZK' => 100
    ];


    /*
     * Set your fiat currency min amount
     */
    const SWITCH_EQUAL_OR_GREATER_BY_FIAT = [
        'USD' => 10,
        'CZK' => 100
    ];

    /*
     * Switch statuses by response code;
     */
    const STATUSES = [0 => "Requested sell payment",
        1 => "Sell payment sent",
        2 => "Error",
        3 => "Sell payment received - You can withdraw your money"];

    /*
     * Setup crypto_name used for QR code generator;
     * Terminal give you only cryptocurrency abbreviation but
     * QR code generator needs full name. Switch helps you make it.
     */
    const SWITCH_CRYPTO_NAME = [
        'BTC' => 'bitcoin',
        'LTC' => 'litecoin'
    ];

    public function terminals();

    public function sellCrypto($serialNumber, $fiatAmount, $fiatCurrency, $cryptoCurrency);

    public function terminalsWithAvailableCash($fiatAmount, $fiatCurrency);

    public function status($transactionId);

}
