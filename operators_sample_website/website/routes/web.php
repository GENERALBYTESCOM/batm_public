<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\HomeController;
use App\Http\Controllers\SellController;


/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', [HomeController::class, 'index']);

Route::get('/trade', [HomeController::class, 'show']);

Route::get('/sell/show', [SellController::class, 'show']);

Route::get('/sell/create', [SellController::class, 'create']);

Route::get('/sell/index', [SellController::class, 'index']);

Route::get('/status', [SellController::class,'status']);
