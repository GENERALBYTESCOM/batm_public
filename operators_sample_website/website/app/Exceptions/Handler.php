<?php

namespace App\Exceptions;

use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Throwable;

class Handler extends ExceptionHandler
{
    /**
     * A list of the exception types that are not reported.
     *
     * @var array
     */
    protected $dontReport = [
        //
    ];

    /**
     * A list of the inputs that are never flashed for validation exceptions.
     *
     * @var array
     */
    protected $dontFlash = [
        'current_password',
        'password',
        'password_confirmation',
    ];

    /**
     * Register the exception handling callbacks for the application.
     *
     * @return void
     */
    public function register()
    {
        $this->reportable(function (Throwable $e) {
            //
        });
    }

    /**
     * @param Request $request
     * @param Throwable $e
     * @return JsonResponse|Response|\Symfony\Component\HttpFoundation\Response
     * @throws Throwable
     */
    public function render($request, Throwable $e)
    {
        if ($e instanceof AuthenticationException) {
            $e->report();
            return $e->render($request);
        }

        if ($e instanceof TerminalsWithAvailableCashException) {
            $e->report($e);
            return $e->render($request, $e);
        }

        if ($e instanceof TerminalsException) {
            $e->report($e);
            return $e->render($request, $e);
        }

        if ($e instanceof TransactionStatusException) {
            $e->report($e);
        }

        if ($e instanceof SellTransactionException) {
            $e->report($e);
            return $e->render($request, $e);
        }
        return parent::render($request, $e);
    }

}
