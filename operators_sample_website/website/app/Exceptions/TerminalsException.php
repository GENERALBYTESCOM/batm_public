<?php

namespace App\Exceptions;

use Exception;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Log;

class TerminalsException extends Exception
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
     * @return Response
     */
    public function render(Request $request, Exception $e): Response
    {
        $error = $e->getMessage();
        return response()->view('homePage', compact('error'));
    }
}
