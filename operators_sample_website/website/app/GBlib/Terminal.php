<?php

namespace App\GBlib;

class Terminal
{
    public $active; // bool
    public $allowedCashCurrencies; // array
    public $allowedCryptoCurrencies; // array
    public $connectedAt; // string
    public $deleted; // bool
    public $errors; //int
    public $exchangeRatesSell; // string
    public $exchangeRatesBuy; // string
    public $exchangeRateUpdatedAt; // string
    public $lastPingAt; //string
    public $lastPingDuration; // int
    public $location; // object
    public $locked; // bool
    public $name;
    public $operationalMode; // int
    public $rejectedReason; // int
    public $serialNumber; // string
    public $type;
}
