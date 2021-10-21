<?php

namespace App\GBlib\Repositories;

use App\Exceptions\SellTransactionException;
use App\Exceptions\TerminalsException;
use App\Exceptions\TerminalsWithAvailableCashException;
use Illuminate\Support\Facades\Http;
use App\Exceptions\AuthenticationException;

class HttpClientRequests implements IGbCasRestServices
{

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function terminals()
    {
        try {
            $response = Http::withoutVerifying()->withHeaders(['X-Api-Key' => self::API_KEY])->get(self::TERMINALS);
        } catch (\Exception $e) {
            throw new TerminalsException(self::TERMINALS . " - Connection error");
        }
        $this->responseStatusValidation($response, self::TERMINALS);
        return $this->jsonDecode($response);
    }

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    private function responseStatusValidation($response, string $url): void
    {
        $status = $response->status();
        if ($status == 401) {
            throw new AuthenticationException();
        }
        if ($status != 200) {
            $message = $url . " - Wrong status : " . $status;
            throw new TerminalsException($message);
        }
    }

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     * @throws TerminalsWithAvailableCashException
     */
    public function terminalsWithAvailableCash($fiatAmount, $fiatCurrency)
    {
        try {
            $response = Http::withoutVerifying()->withHeaders(['X-Api-Key' => self::API_KEY])->get(self::TERMINALS_WITH_AVAILABLE_CASH,
                [
                    'amount' => $fiatAmount,
                    'fiat_currency' => $fiatCurrency
                ]);
        } catch (\Exception $e) {
            throw new TerminalsWithAvailableCashException(self::TERMINALS_WITH_AVAILABLE_CASH . " - Connection error");
        }
        $this->responseStatusValidation($response, self::TERMINALS_WITH_AVAILABLE_CASH);
        return $this->jsonDecode($response);
    }

    /**
     * @throws AuthenticationException
     * @throws SellTransactionException
     * @throws TerminalsException
     */
    public function sellCrypto($serialNumber, $fiatAmount, $fiatCurrency, $cryptoCurrency)
    {
        try {
            $response = Http::withoutVerifying()->withHeaders(['X-Api-Key' => self::API_KEY])->get(self::SELL_CRYPTO, [
                'serial_number' => $serialNumber,
                'fiat_amount' => $fiatAmount,
                'fiat_currency' => $fiatCurrency,
                'crypto_amount' => 0.0,
                'crypto_currency' => $cryptoCurrency
            ]);
        } catch (\Exception $e) {
            throw new SellTransactionException(self::SELL_CRYPTO . " - Connection error");
        }

        $this->responseStatusValidation($response, self::TERMINALS_WITH_AVAILABLE_CASH);

        return $this->jsonDecode($response);
    }

    /**
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function status($transactionId): string
    {
        try {
            $response = Http::withoutVerifying()->withHeaders(['X-Api-Key' => self::API_KEY])->get(self::STATUS, [
                'transaction_id' => $transactionId
            ]);
        } catch (\Exception $e) {
            throw new TerminalsException(self::STATUS . " - Connection_error ");
        }
        $this->responseStatusValidation($response, self::STATUS);
        return $this->transferTransactionStatus($response->body());
    }

    /**
     * @throws TerminalsException
     */
    private function jsonDecode($response)
    {
        try {
            $body = $response->body();
            $res = json_decode($body);
        } catch (\Exception $e) {
            throw new TerminalsException("Not JSON format");
        }
        return $res;
    }

    public static function transferTransactionStatus($transactionId): string
    {
        return self::STATUSES[$transactionId];
    }
}
