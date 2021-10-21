<?php

namespace App\Http\Controllers;

use App\Exceptions\AuthenticationException;
use App\Exceptions\SellTransactionException;
use App\Exceptions\TerminalsException;
use App\Exceptions\TerminalsWithAvailableCashException;
use App\Exceptions\TransactionStatusException;
use App\GBlib\Terminals;
use Illuminate\Contracts\Foundation\Application;
use Illuminate\Contracts\View\Factory;
use Illuminate\Contracts\View\View;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use App\GBlib\Repositories\IGbCasRestServices;
use Illuminate\Support\Facades\Redirect;

class SellController extends Controller
{
    private $fiat;

    /**
     * @return Application|Factory|View
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function index()
    {
        $terminalsInst = new Terminals();
        $terminals = $terminalsInst->searchTerminals(true);
        $cryptoCurrencies = $terminalsInst->allowedCryptoCurrencies;
        $fiats = $terminalsInst->allowedCashCurrencies;

        return view('sellAmountFiatCrypto', compact('fiats', 'cryptoCurrencies', 'terminals'));
    }

    /**
     * @param Request $request
     * @return Application|Factory|View
     * @throws AuthenticationException
     * @throws TerminalsException
     * @throws TerminalsWithAvailableCashException
     */
    public function show(Request $request)
    {
        $this->showValidation($request);
        $amount = $request->amount;
        $fiatCurrency = $request->fiat;
        $cryptoCurrency = $request->cryptoCurrency;

        $terminals = (new Terminals())->getTerminalsWithAvailableCash($amount, $fiatCurrency);
        return view('terminalsWithAvailableCash', compact('terminals', 'amount', 'fiatCurrency', 'cryptoCurrency'));
    }


    /**
     * @param Request $request
     * @return Application|Factory|View|RedirectResponse
     * @throws SellTransactionException
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function create(Request $request)
    {
        $this->createValidation($request);

        $amount = $request->fiatAmount;
        $fiatCurrency = $request->fiatCurrency;
        $cryptoCurrency = $request->cryptoCurrency;
        $terminalSerialNumber = $request->selectedTerminal;

        $sellInfo = (new Terminals())->sellCrypto($terminalSerialNumber, $amount, $fiatCurrency, $cryptoCurrency);
        $cryptoFullName = IGbCasRestServices::SWITCH_CRYPTO_NAME[$cryptoCurrency];
        if ($sellInfo) {
            return view('createSellTransaction', compact('amount', 'fiatCurrency', 'cryptoCurrency', 'sellInfo', 'cryptoFullName'));
        }
        return Redirect::to("/");
    }

    /**
     * @param Request $request
     * @return JsonResponse
     * @throws TransactionStatusException
     */
    public function status(Request $request): JsonResponse
    {
        $status = (new Terminals())->getStatus($request->transaction_id);
        return response()->json(['success' => $status]);
    }

    private function showValidation($request)
    {
        $validateFiat = $request->validate([
            'fiat' => ['required', 'max:60']
        ]);
        $this->fiat = $request->fiat;
        $validatedData = $request->validate([
            'cryptoCurrency' => ['required', 'max:60'],
            'amount' => ['required', 'numeric',
                function ($attribute, $value, $fail) {
                    $banknoteDenomination = IGbCasRestServices::SWITCH_BANKNOTE_DENOMINATION_BY_FIAT[$this->fiat];
                    if ($value % $banknoteDenomination !== 0) {
                        $fail($attribute . ' must be ' . $banknoteDenomination . ' divisible');
                    }
                },
                'gte:' . IGbCasRestServices::SWITCH_EQUAL_OR_GREATER_BY_FIAT[$this->fiat]],
        ]);
    }

    /**
     * @param $request
     */
    private function createValidation($request)
    {
        $validateFiat = $request->validate([
            'fiatCurrency' => ['required', 'max:60']
        ]);
        $this->fiat = $request->fiatCurrency;
        $validateFiat = $request->validate([
            'cryptoCurrency' => ['required', 'max:60'],
            'selectedTerminal' => ['required', 'max:30'],
            'fiatAmount' => ['required', 'numeric',
                // Setup validator for banknote denomination
                function ($attribute, $value, $fail) {
                    $banknoteDenomination = IGbCasRestServices::SWITCH_BANKNOTE_DENOMINATION_BY_FIAT[$this->fiat];
                    if ($value % $banknoteDenomination !== 0) {
                        $fail($attribute . ' must be ' . $banknoteDenomination . ' divisible');
                    }
                },
                'gte:' . IGbCasRestServices::SWITCH_EQUAL_OR_GREATER_BY_FIAT[$this->fiat]]
        ]);
    }
}
