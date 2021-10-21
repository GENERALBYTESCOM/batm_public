<?php
namespace App\Http\Controllers;

use App\Exceptions\AuthenticationException;
use App\Exceptions\TerminalsException;
use Illuminate\Contracts\Foundation\Application;
use Illuminate\Contracts\View\Factory;
use Illuminate\Contracts\View\View;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use App\GBlib\Terminals;
use Illuminate\Support\Facades\Redirect;

class HomeController extends Controller
{
    /**
     * @return Application|Factory|View
     * @throws AuthenticationException
     * @throws TerminalsException
     */
    public function index()
    {
        $terminals = (new Terminals())->searchTerminals();
        return view('homePage', compact('terminals'));
    }

    /**
     * @param Request $request
     * @return RedirectResponse
     */
    public function show(Request $request): RedirectResponse
    {
        if ($request->input('sell')) {
            return Redirect::to("/sell/index");
        } else {
            return Redirect::to('/#locations');
        }
    }
}
