<?php

namespace App\Exceptions;

use Exception;
use Illuminate\Support\Facades\Log;

class TransactionStatusException extends Exception
{
    /**
     * @param Exception $e
     */
    public function report(Exception $e)
    {
        Log::debug($e->getMessage());
    }
}
