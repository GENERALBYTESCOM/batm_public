<?php

namespace App\Exceptions;

use Exception;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Redirect;
use Illuminate\Support\Facades\Log;

class TerminalsWithAvailableCashException extends Exception
{
    /**
     * @param Exception $e
     */
    public function report(Exception $e)
    {

        Log::debug($e->getMessage());
    }

    /**
     * @param Request $request
     * @param Exception $e
     * @return RedirectResponse
     */
    public function render(Request $request, Exception $e): RedirectResponse
    {
        return Redirect::back()->withErrors(['error' => $e->getMessage()]);
    }
}
