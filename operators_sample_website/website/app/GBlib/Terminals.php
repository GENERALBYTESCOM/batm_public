<?php

namespace App\GBlib;

use App\Exceptions\SellTransactionException;
use App\Exceptions\TerminalsException;
use App\Exceptions\AuthenticationException;
use App\Exceptions\TerminalsWithAvailableCashException;
use App\Exceptions\TransactionStatusException;
use App\GBlib\Repositories\HttpClientRequests;

class Terminals
{
    public $terminals = array(); // array of terminals
    public $allowedCashCurrencies = array(); // array of all allowed cash currencies from terminals
    public $allowedCryptoCurrencies = array(); // array pof all allowed crypto from all terminals

    public $httpClientRequests; // Methods for sending request to CAS


    public function __construct()
    {
        $this->httpClientRequests = new HttpClientRequests();
    }

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function searchTerminals(bool $withSellData = false): array
    {
        $terminals = $this->httpClientRequests->terminals();
        try {
            $this->mapTerminals($terminals);
            $this->allowedCashCurrencies = $this->createAllowedCashCurrencies($this->terminals);
            $this->allowedCryptoCurrencies = $this->createAllowedCryptoCurrencies($this->terminals);
        } catch (\Exception $e) {
            throw new TerminalsException("Error - terminals not loaded");

        }
        $this->validateTerminals($this->terminals, $withSellData);
        return $this->terminals;
    }

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     * @throws TerminalsWithAvailableCashException
     */
    public function getTerminalsWithAvailableCash($fiatAmount, $fiatCurrency): array
    {
        $terminals = $this->httpClientRequests->terminalsWithAvailableCash($fiatAmount, $fiatCurrency);
        try {
            $this->mapTerminals($terminals);
            $this->allowedCashCurrencies = $this->createAllowedCashCurrencies($this->terminals);
            $this->allowedCryptoCurrencies = $this->createAllowedCryptoCurrencies($this->terminals);
        } catch (\Exception $e) {
            throw new TerminalsException("Error - terminalsWithAvailableCash not loaded");
        }
        $this->validateTerminalsWithAvailableCash($this->terminals, true);
        return $this->terminals;
    }

    /**
     * @throws SellTransactionException
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function sellCrypto($serialNumber, $fiatAmount, $fiatCurrency, $cryptoCurrency)
    {

        $response = $this->httpClientRequests->sellCrypto($serialNumber, $fiatAmount, $fiatCurrency, $cryptoCurrency);
        $this->validateSellCrypto($response);
        return $response;
    }

    /**
     * @throws SellTransactionException
     */
    private function validateSellCrypto($response) {
        if(empty($response)) {
            throw new SellTransactionException("Missing sell crypto data");
        }
    }


    /**
     * @throws TransactionStatusException
     */
    public function getStatus($transactionId): string
    {
        try {
            return $this->httpClientRequests->status($transactionId);

        } catch (\Exception $e) {
            $message = "Error status transaction Id: $transactionId";
            throw new TransactionStatusException($message);
        }
    }


    /**
     * @throws TerminalsException
     */
    public function validateTerminals($terminals, $withSellData): bool
    {
        if (empty($terminals)) {
            throw new TerminalsException("No terminals found");
        }

        if ($withSellData) {
            if (empty($this->allowedCashCurrencies)) {
                throw new TerminalsException("No allowed cash currency");
            }

            if (empty($this->allowedCryptoCurrencies)) {
                throw new TerminalsException("No allowed crypto currency");
            }
        }
        return true;
    }

    /**
     * @throws TerminalsWithAvailableCashException
     */
    public function validateTerminalsWithAvailableCash($terminals, $withSellData): bool
    {
        if (empty($terminals)) {
            throw new TerminalsWithAvailableCashException("No terminals found");
        }
        return true;
    }


    private function createAllowedCashCurrencies($terminals): array
    {
        $allowedCashCurrencies = array();
        foreach ($terminals as $terminal) {
            foreach ($terminal->allowedCashCurrencies as $fiat)
                if (!in_array($fiat, $allowedCashCurrencies)) {
                    $allowedCashCurrencies[] = $fiat;
                }
        }
        return $allowedCashCurrencies;
    }

    private function createAllowedCryptoCurrencies($terminals): array
    {
        $allowedCryptoCurrencies = array();
        foreach ($terminals as $terminal) {
            foreach ($terminal->allowedCryptoCurrencies as $crypto)
                if (!in_array($crypto, $allowedCryptoCurrencies)) {
                    $allowedCryptoCurrencies[] = $crypto;
                }
        }
        return $allowedCryptoCurrencies;
    }


    private function mapTerminals($response)
    {
        foreach ($response as $dat) {
            $terminal = new Terminal();
            $terminal->active = ($dat->active);
            $terminal->allowedCashCurrencies = ($dat->allowedCashCurrencies);
            $terminal->allowedCryptoCurrencies = ($dat->allowedCryptoCurrencies);
            $terminal->connectedAt = ($dat->connectedAt);
            $terminal->deleted = ($dat->deleted);
            $terminal->errors = ($dat->errors);
            $terminal->exchangeRatesBuy = ($dat->exchangeRatesBuy);
            $terminal->exchangeRatesSell = ($dat->exchangeRatesSell);
            $terminal->exchangeRateUpdatedAt = ($dat->active);
            $terminal->lastPingAt = ($dat->lastPingAt);
            $terminal->lastPingDuration = ($dat->lastPingDuration);
            $terminal->location = ($dat->location);
            $terminal->locked = ($dat->locked);
            $terminal->name = ($dat->name);
            $terminal->operationalMode = ($dat->operationalMode);
            $terminal->rejectedReason = ($dat->rejectedReason);
            $terminal->serialNumber = ($dat->serialNumber);
            $terminal->type = ($dat->type);

            $this->terminals[$dat->serialNumber] = $terminal;
        }
    }

}
