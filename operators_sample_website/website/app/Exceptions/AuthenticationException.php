<?php

namespace App\Exceptions;

use Exception;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class AuthenticationException extends Exception
{

    public function report()
    {
        //
    }

    /**
     * @param Request $request
     * @return Response
     */
    public function render(Request $request): Response
    {
        $error = ('Wrong authentication - check your X-Api-Key');

        return response()->view('homePage', compact( 'error'));
    }
}
